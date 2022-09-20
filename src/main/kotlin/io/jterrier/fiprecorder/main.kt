package io.jterrier.fiprecorder

import io.jterrier.fiprecorder.formats.kotlinXMessage
import io.jterrier.fiprecorder.formats.kotlinXMessageLens
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
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime

val logger: Logger = LoggerFactory.getLogger("io.jterrier.fiprecorder.Main")

val app: HttpHandler = routes(
    "/ping" bind GET to {
        Response(OK).body("pong")
    },

    "/formats/json/kotlinx" bind GET to {
        Response(OK).with(kotlinXMessageLens of kotlinXMessage)
    },

    "/templates/handlebars" bind GET to {
        val renderer = HandlebarsTemplates().CachingClasspath()
        val view = Body.viewModel(renderer, TEXT_HTML).toLens()
        val viewModel = HandlebarsViewModel("Hello there!")
        Response(OK).with(view of viewModel)
    },

    "/testing/kotest" bind GET to { request ->
        Response(OK).body("Echo '${request.bodyString()}'")
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