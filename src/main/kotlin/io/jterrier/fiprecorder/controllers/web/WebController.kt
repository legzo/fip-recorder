package io.jterrier.fiprecorder.controllers.web

import io.jterrier.fiprecorder.services.TrackService
import io.jterrier.fiprecorder.controllers.web.models.ListOfTracksViewModel
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import org.slf4j.LoggerFactory
import java.time.LocalDate

class WebController(
    private val trackService: TrackService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val renderer = HandlebarsTemplates().HotReload("src/main/resources/templates")

    fun showTracksForDate(request: Request): Response {
        val view = Body.viewModel(renderer, ContentType.TEXT_HTML).toLens()
        val dateAsString = request.query("date")
        val localDate = LocalDate.parse(dateAsString)
        logger.info("Treating date : $localDate")
        val tracks = trackService.getTracksForDate(localDate)
        val stats = trackService.getStatisticsForTracks(tracks)

        val viewModel = ListOfTracksViewModel.from(localDate, tracks, stats)
        return Response(OK).with(view of viewModel)
    }

}