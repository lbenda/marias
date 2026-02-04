package cz.lbenda.games.marias.server.routes

import cz.lbenda.games.marias.server.dto.*
import cz.lbenda.games.marias.server.service.GameService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.gameRoutes(service: GameService) {
    route("/games") {
        post {
            val req = call.receive<CreateGameRequest>()
            val state = service.create(req.playerId, req.playerName)
            call.respond(HttpStatusCode.Created, state.toResponse())
        }

        get { call.respond(service.all().map { it.toListItem() }) }

        get("/{id}") {
            val state = service.get(call.parameters["id"]!!)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Game not found")
            call.respond(state.toResponse())
        }

        delete("/{id}") {
            if (service.delete(call.parameters["id"]!!)) call.respond(HttpStatusCode.NoContent)
            else call.respond(HttpStatusCode.NotFound, "Game not found")
        }

        post("/{id}/actions") {
            val req = call.receive<ActionRequest>()
            val state = service.dispatch(call.parameters["id"]!!, req.action)
                ?: return@post call.respond(HttpStatusCode.NotFound, "Game not found")
            call.respond(state.toResponse())
        }

        get("/{id}/players/{playerId}/hand") {
            val state = service.get(call.parameters["id"]!!)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Game not found")
            call.respond(state.handResponse(call.parameters["playerId"]!!))
        }

        put("/{id}/players/{playerId}/hand") {
            val gameId = call.parameters["id"]!!
            val playerId = call.parameters["playerId"]!!
            val req = call.receive<ReorderHandRequest>()

            val state = service.dispatch(
                gameId,
                cz.lbenda.games.marias.engine.action.GameAction.ReorderHand(playerId, req.cards)
            ) ?: return@put call.respond(HttpStatusCode.NotFound, "Game not found")

            if (state.error != null) {
                return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to state.error))
            }
            call.respond(state.handResponse(playerId))
        }

        get("/{id}/talon") {
            val state = service.get(call.parameters["id"]!!)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Game not found")
            val playerId = call.request.queryParameters["playerId"]
            if (playerId != state.declarerId) {
                return@get call.respond(HttpStatusCode.Forbidden, "Not declarer")
            }
            call.respond(mapOf("cards" to state.talon))
        }

        get("/{id}/bidding") {
            val state = service.get(call.parameters["id"]!!)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Game not found")
            call.respond(state.biddingResponse())
        }
    }
}
