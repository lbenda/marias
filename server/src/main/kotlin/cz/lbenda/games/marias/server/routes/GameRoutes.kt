package cz.lbenda.games.marias.server.routes

import cz.lbenda.games.marias.server.dto.*
import cz.lbenda.games.marias.server.service.GameService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.gameRoutes(gameService: GameService) {

    route("/games") {

        // Create new game
        post {
            val request = call.receive<CreateGameRequest>()
            val state = gameService.createGame(request.creatorPlayerId, request.creatorPlayerName)
            val response = CreateGameResponse(
                gameId = state.gameId,
                state = DtoMapper.toGameStateDto(state)
            )
            call.respond(HttpStatusCode.Created, response)
        }

        // List all games
        get {
            val games = gameService.getAllGames()
            val response = GameListResponse(
                games = games.map { DtoMapper.toGameSummaryDto(it) }
            )
            call.respond(response)
        }

        // Get specific game state
        get("/{gameId}") {
            val gameId = call.parameters["gameId"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing game ID")
            )

            val state = gameService.getGame(gameId) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to "Game not found")
            )

            call.respond(DtoMapper.toGameStateDto(state))
        }

        // Delete a game
        delete("/{gameId}") {
            val gameId = call.parameters["gameId"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing game ID")
            )

            val deleted = gameService.deleteGame(gameId)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Game not found"))
            }
        }

        // Dispatch action
        post("/{gameId}/actions") {
            val gameId = call.parameters["gameId"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing game ID")
            )

            val request = call.receive<ActionRequest>()
            val newState = gameService.dispatchAction(gameId, request.action)

            if (newState == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Game not found"))
                return@post
            }

            val response = ActionResponse(
                success = newState.errorMessage == null,
                state = DtoMapper.toGameStateDto(newState),
                errorMessage = newState.errorMessage
            )

            call.respond(response)
        }

        // Get player's hand
        get("/{gameId}/players/{playerId}/hand") {
            val gameId = call.parameters["gameId"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing game ID")
            )
            val playerId = call.parameters["playerId"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing player ID")
            )

            val state = gameService.getGame(gameId) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to "Game not found")
            )

            val hand = gameService.getPlayerHand(gameId, playerId) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to "Player not found")
            )

            val validCards = gameService.getValidCards(gameId, playerId)
            call.respond(DtoMapper.toPlayerHandDto(state, playerId, validCards))
        }

        // Get talon (only for declarer during exchange)
        get("/{gameId}/talon") {
            val gameId = call.parameters["gameId"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing game ID")
            )
            val playerId = call.request.queryParameters["playerId"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing player ID")
            )

            val talon = gameService.getTalon(gameId, playerId)
            if (talon == null) {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Cannot view talon"))
                return@get
            }

            call.respond(TalonDto(talon))
        }

        // Get bidding state
        get("/{gameId}/bidding") {
            val gameId = call.parameters["gameId"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Missing game ID")
            )

            val state = gameService.getGame(gameId) ?: return@get call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to "Game not found")
            )

            call.respond(DtoMapper.toBiddingStateDto(state))
        }
    }
}
