package io.jterrier.fiprecorder.controllers

import io.jterrier.fiprecorder.apis.FipApiConnector
import io.jterrier.fiprecorder.apis.models.FipSong
import io.jterrier.fiprecorder.database.DatabaseConnector
import io.jterrier.fiprecorder.models.Track
import io.jterrier.fiprecorder.services.TrackService
import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.slf4j.LoggerFactory
import java.time.LocalDate

class SongsController(
    private val trackService: TrackService,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val songListLens = Body.auto<List<Track>>().toLens()

    fun loadSongsForDay(request: Request): Response {
        val dateAsString = request.query("date")
        val localDate = LocalDate.parse(dateAsString)
        logger.info("Treating date : $localDate")

        val tracks = trackService.getTracksForDate(localDate)
        return songListLens.inject(tracks, Response(Status.OK))
    }

}