package cz.lbenda.games.marias.server.routes

import cz.lbenda.games.marias.server.dto.toResponse
import cz.lbenda.games.marias.server.service.GameService
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class WebSocketMessage(
    val type: String,
    val version: Long? = null,
    val data: cz.lbenda.games.marias.server.dto.GameResponse? = null,
    val message: String? = null
)

private val json = Json {
    prettyPrint = false
    encodeDefaults = true
    ignoreUnknownKeys = true
}

fun Route.webSocketRoutes(service: GameService) {
    route("/games") {
        /**
         * WebSocket endpoint for real-time game updates.
         *
         * On connect: sends current state immediately
         * On state change: broadcasts new state to all connected clients
         */
        webSocket("/{id}/ws") {
            val gameId = call.parameters["id"]!!
            println("WebSocket connection attempt for game: $gameId")

            // Check if game exists
            val initialState = service.get(gameId)
            if (initialState == null) {
                println("WebSocket: Game not found: $gameId")
                val errorMsg = WebSocketMessage(type = "error", message = "Game not found")
                send(Frame.Text(json.encodeToString(errorMsg)))
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Game not found"))
                return@webSocket
            }

            println("WebSocket: Sending initial state for game: $gameId")
            // Send initial state
            val initialMsg = WebSocketMessage(
                type = "state",
                version = initialState.version,
                data = initialState.toResponse()
            )
            send(Frame.Text(json.encodeToString(initialMsg)))

            // Subscribe to game events and forward to client
            val subscriptionJob = launch {
                service.subscribe(gameId)
                    .onEach { state ->
                        val msg = WebSocketMessage(
                            type = "state",
                            version = state.version,
                            data = state.toResponse()
                        )
                        send(Frame.Text(json.encodeToString(msg)))
                    }
                    .catch { e ->
                        println("WebSocket subscription error for game $gameId: ${e.message}")
                    }
                    .collect()
            }

            try {
                // Keep connection alive and handle incoming messages
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            // Could handle client actions here in the future
                        }
                        is Frame.Ping -> send(Frame.Pong(frame.data))
                        else -> { /* ignore other frame types */ }
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                println("WebSocket closed normally for game: $gameId")
            } catch (e: Exception) {
                println("WebSocket error for game $gameId: ${e.message}")
            } finally {
                println("WebSocket disconnected for game: $gameId")
                subscriptionJob.cancel()
            }
        }
    }
}
