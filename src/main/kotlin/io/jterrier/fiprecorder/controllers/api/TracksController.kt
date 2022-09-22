package io.jterrier.fiprecorder.controllers.api

import io.jterrier.fiprecorder.models.Statistics
import io.jterrier.fiprecorder.models.Track
import io.jterrier.fiprecorder.models.weekNb
import io.jterrier.fiprecorder.services.StatsService
import io.jterrier.fiprecorder.services.TrackService
import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.slf4j.LoggerFactory
import java.time.LocalDate

class TracksController(
    private val trackService: TrackService,
    private val statsService: StatsService,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val trackListLens = Body.auto<List<Track>>().toLens()
    private val statsLens = Body.auto<Statistics>().toLens()

    fun loadTracksForDay(request: Request): Response {
        val dateAsString = request.query("date")
        val localDate = LocalDate.parse(dateAsString)
        logger.info("Treating date : $localDate")

        val tracks = trackService.getTracksForDate(localDate)
        return trackListLens.inject(tracks, Response(Status.OK))
    }

    fun getStatsForWeek(request: Request): Response {
        val year = request.query("year")?.toInt()
        val weekIndex = request.query("week")?.toInt()

        if (year == null || weekIndex == null)
            return Response(Status.BAD_REQUEST)

        logger.info("Treating week : $year-$weekIndex")

        val tracks = trackService.getTracksForWeek(year.weekNb(weekIndex))
        val stats = statsService.getStatisticsForTracks(tracks, 10)
        return statsLens.inject(stats, Response(Status.OK))
    }

}