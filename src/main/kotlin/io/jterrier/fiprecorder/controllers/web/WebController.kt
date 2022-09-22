package io.jterrier.fiprecorder.controllers.web

import io.jterrier.fiprecorder.controllers.dateQuery
import io.jterrier.fiprecorder.controllers.web.models.DayViewModel
import io.jterrier.fiprecorder.controllers.web.models.WeekViewModel
import io.jterrier.fiprecorder.controllers.weekQuery
import io.jterrier.fiprecorder.controllers.yearQuery
import io.jterrier.fiprecorder.models.weekNb
import io.jterrier.fiprecorder.services.StatsService
import io.jterrier.fiprecorder.services.TrackService
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import org.slf4j.LoggerFactory

class WebController(
    private val trackService: TrackService,
    private val statsService: StatsService,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    //private val renderer = HandlebarsTemplates().CachingClasspath("templates")
    private val renderer = HandlebarsTemplates().HotReload("src/main/resources/templates")

    fun showTracksForDate(request: Request): Response {
        val view = Body.viewModel(renderer, ContentType.TEXT_HTML).toLens()

        val localDate = dateQuery(request)
        logger.info("Treating date : $localDate")

        val tracks = trackService.getTracksForDate(localDate)
        val stats = statsService.getStatisticsForTracks(tracks, itemNbForTops = 5)

        val viewModel = DayViewModel.from(localDate, tracks, stats)
        return Response(OK).with(view of viewModel)
    }

    fun showWeek(request: Request) :Response {
        val view = Body.viewModel(renderer, ContentType.TEXT_HTML).toLens()

        val year = yearQuery(request)
        val weekIndex = weekQuery(request)

        logger.info("Treating week : $year-$weekIndex")

        val week = year.weekNb(weekIndex)

        val tracks = trackService.getTracksForWeek(week)
        val stats = statsService.getStatisticsForTracks(tracks, itemNbForTops = 5)

        val topTracks = statsService.getTopTracks(tracks, 30)

        val viewModel = WeekViewModel.from(week, topTracks, stats)
        return Response(OK).with(view of viewModel)
    }

}