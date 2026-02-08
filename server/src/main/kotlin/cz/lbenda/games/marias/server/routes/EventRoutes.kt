package cz.lbenda.games.marias.server.routes

import cz.lbenda.games.marias.server.dto.toResponse
import cz.lbenda.games.marias.server.service.GameService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/** Maximum wait time for long polling (seconds) */
private const val MAX_WAIT_SECONDS = 60L

/** Default wait time if Prefer header not specified (short polling) */
private const val DEFAULT_WAIT_SECONDS = 0L

fun Route.eventRoutes(service: GameService) {
    route("/games/{id}") {
        /**
         * Polling endpoint for game events.
         *
         * Request headers:
         * - If-None-Match: v{version} - Version client has
         * - Prefer: wait={seconds} - Enable long polling
         *
         * Response:
         * - 200 OK + state if new data available
         * - 304 Not Modified if version matches (and timeout for long poll)
         * - 404 Not Found if game doesn't exist
         */
        get("/events") {
            val gameId = call.parameters["id"]!!

            // Get current state first to check if game exists
            val currentState = service.get(gameId)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Game not found")

            // Parse If-None-Match header (format: v{version} or "{version}")
            val ifNoneMatch = call.request.headers["If-None-Match"]
            val clientVersion = parseVersion(ifNoneMatch)

            // Parse Prefer header for wait time
            val prefer = call.request.headers["Prefer"]
            val waitSeconds = parseWaitTime(prefer).coerceAtMost(MAX_WAIT_SECONDS)

            // Set cache control headers
            call.response.headers.append(HttpHeaders.CacheControl, "no-store")

            // If no client version or client is outdated, return current state immediately
            if (clientVersion == null || currentState.version > clientVersion) {
                val playerId = call.request.queryParameters["playerId"]
                call.response.headers.append(HttpHeaders.ETag, "v${currentState.version}")
                return@get call.respond(currentState.toResponse(playerId))
            }

            // Client is up to date
            if (waitSeconds <= 0) {
                // Short polling: return 304 immediately
                call.response.headers.append(HttpHeaders.ETag, "v${currentState.version}")
                return@get call.respond(HttpStatusCode.NotModified)
            }

            // Long polling: wait for changes
            val timeoutMs = waitSeconds * 1000
            val newState = service.waitForChange(gameId, clientVersion, timeoutMs)

            if (newState != null) {
                val playerId = call.request.queryParameters["playerId"]
                call.response.headers.append(HttpHeaders.ETag, "v${newState.version}")
                call.respond(newState.toResponse(playerId))
            } else {
                // Timeout, no changes
                call.response.headers.append(HttpHeaders.ETag, "v${currentState.version}")
                call.respond(HttpStatusCode.NotModified)
            }
        }
    }
}

/**
 * Parse version from If-None-Match header.
 * Supports formats: v123, "v123", 123, "123"
 */
private fun parseVersion(header: String?): Long? {
    if (header == null) return null
    val cleaned = header.trim().removeSurrounding("\"").removePrefix("v")
    return cleaned.toLongOrNull()
}

/**
 * Parse wait time from Prefer header.
 * Supports format: wait=30
 */
private fun parseWaitTime(header: String?): Long {
    if (header == null) return DEFAULT_WAIT_SECONDS
    val waitMatch = Regex("""wait=(\d+)""").find(header)
    return waitMatch?.groupValues?.get(1)?.toLongOrNull() ?: DEFAULT_WAIT_SECONDS
}
