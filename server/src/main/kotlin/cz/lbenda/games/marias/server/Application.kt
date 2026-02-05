package cz.lbenda.games.marias.server

import cz.lbenda.games.marias.server.event.InMemoryEventBus
import cz.lbenda.games.marias.server.routes.eventRoutes
import cz.lbenda.games.marias.server.routes.gameRoutes
import cz.lbenda.games.marias.server.routes.webSocketRoutes
import cz.lbenda.games.marias.server.service.GameService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.CacheControl)
        allowHeader(HttpHeaders.IfNoneMatch)
        allowHeader("Prefer")
        allowHeader(HttpHeaders.Upgrade)
        allowHeader(HttpHeaders.Connection)
        exposeHeader(HttpHeaders.ETag)
        exposeHeader(HttpHeaders.CacheControl)
        anyHost()
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to (cause.message ?: "Unknown error"))
            )
        }
    }

    install(WebSockets) {
        pingPeriod = 30.seconds
        timeout = 60.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val eventBus = InMemoryEventBus()
    val gameService = GameService(eventBus)

    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        webSocketRoutes(gameService)  // WebSocket must be registered first
        gameRoutes(gameService)
        eventRoutes(gameService)
    }
}
