package io.jterrier.fiprecorder

import io.jterrier.fiprecorder.apis.FipApiConnector
import io.jterrier.fiprecorder.apis.SpotifyApiConnector
import io.jterrier.fiprecorder.controllers.PlaylistsController
import io.jterrier.fiprecorder.controllers.SongsController
import io.jterrier.fiprecorder.database.DatabaseConnector
import io.jterrier.fiprecorder.services.PlaylistService
import io.jterrier.fiprecorder.services.TrackService
import io.jterrier.fiprecorder.web.WebController
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime

private val logger = LoggerFactory.getLogger("io.jterrier.fiprecorder.Main")

private val fipApi = FipApiConnector()
private val spotifyApi = SpotifyApiConnector()
private val db = DatabaseConnector()

private val trackService = TrackService(fipApi, db)
private val playlistService = PlaylistService(spotifyApi)

private val songsController = SongsController(trackService)
private val playlistsController = PlaylistsController(playlistService)
private val webController = WebController()

val app: HttpHandler = routes(
        "/api" bind routes(
            "/songs" bind GET to songsController::loadSongsForDay,
            "/playlists" bind GET to playlistsController::getPlaylists,
        ),
        routes(
            "/templates/handlebars" bind GET to webController::showPage
        )
)

fun main() {
    val server = app.asServer(Jetty(9000)).start()

    kotlin.concurrent.fixedRateTimer(name = "poll", period = 60 * 60 * 1000) { // 1h
        logger.info("Doing the stuff ! @${OffsetDateTime.now()}")
    }

    logger.info("Server started on " + server.port())
}