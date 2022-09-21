package io.jterrier.fiprecorder.controllers

import io.jterrier.fiprecorder.apis.FipApiConnector
import io.jterrier.fiprecorder.apis.models.Song
import io.jterrier.fiprecorder.database.DatabaseConnector
import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.slf4j.LoggerFactory
import java.time.LocalDate

class SongsController(
    private val fipApiConnector: FipApiConnector,
    private val databaseConnector: DatabaseConnector
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val songListLens = Body.auto<List<Song>>().toLens()

    fun loadSongsForDay(request: Request): Response {
        val dateAsString = request.query("date")
        val localDate = LocalDate.parse(dateAsString)
        logger.info("Treating date : $localDate")

        return if (!databaseConnector.isDateDone(localDate)) {
            logger.info("Need to fetch data from Fip")
            val songs = fipApiConnector.getSongsForDay(localDate)
            logger.info("Songs fetched, inserting them")
            databaseConnector.insertSongs(songs, localDate)
            logger.info("Songs inserted, returning to json")
            songListLens.inject(songs, Response(Status.OK))
        } else {
            logger.info("No need to fetch any data")
            Response(Status.OK)
        }
    }

}