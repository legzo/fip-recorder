package io.jterrier.fiprecorder.controllers.api

import io.jterrier.fiprecorder.controllers.dateQuery
import io.jterrier.fiprecorder.controllers.weekQuery
import io.jterrier.fiprecorder.controllers.yearQuery
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

class TracksController(
    private val trackService: TrackService,
    private val statsService: StatsService,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val trackListLens = Body.auto<List<Track>>().toLens()
    private val statsLens = Body.auto<Statistics>().toLens()

    fun loadTracksForDay(request: Request): Response {
        val localDate = dateQuery(request)
        logger.info("Treating date : $localDate")

        val tracks = trackService.getTracksForDate(localDate)
        return trackListLens.inject(tracks, Response(Status.OK))
    }

    fun getStatsForWeek(request: Request): Response {
        val year = yearQuery(request)
        val weekIndex = weekQuery(request)

        logger.info("Treating week : $year-$weekIndex")

        val tracks = trackService.getTracksForWeek(year.weekNb(weekIndex))
        val stats = statsService.getStatisticsForTracks(tracks, 10)
        return statsLens.inject(stats, Response(Status.OK))
    }

}