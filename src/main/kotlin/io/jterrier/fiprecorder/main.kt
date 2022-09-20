package io.jterrier.fiprecorder

import io.jterrier.fiprecorder.models.HandlebarsViewModel
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.OffsetDateTime

private val logger: Logger = LoggerFactory.getLogger("io.jterrier.fiprecorder.Main")

private val songListLens = Body.auto<List<Song>>().toLens()

private val fipApiConnector = FipApiConnector()

val app: HttpHandler = routes(
    "/ping" bind GET to {
        Response(OK).body("pong")
    },

    "/songs" bind GET to {
        val dateAsString = it.query("date")
        val localDate = LocalDate.parse(dateAsString)

        songListLens.inject(fipApiConnector.getSongsForDay(localDate), Response(OK))
    },

    "/templates/handlebars" bind GET to {
        val renderer = HandlebarsTemplates().CachingClasspath()
        val view = Body.viewModel(renderer, TEXT_HTML).toLens()
        val viewModel = HandlebarsViewModel("Hello there!")
        Response(OK).with(view of viewModel)
    },

    )

fun main() {
    val printingApp: HttpHandler = PrintRequest().then(app)

    val server = printingApp.asServer(Jetty(9000)).start()

    kotlin.concurrent.fixedRateTimer(name = "poll", period = 10 * 60 * 1000) { // 10 minutes
        logger.info("Doing the stuff ! @${OffsetDateTime.now()}")
    }

    logger.info("Server started on " + server.port())
}