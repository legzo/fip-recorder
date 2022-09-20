package io.jterrier.fiprecorder

import org.http4k.client.OkHttp
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintResponse
import org.slf4j.LoggerFactory

fun main() {

    val logger = LoggerFactory.getLogger("io.jterrier.fiprecorder.Client")

    val client: HttpHandler = OkHttp()

    val printingClient: HttpHandler = PrintResponse().then(client)

    val response: Response = printingClient(Request(GET, "http://localhost:9000/ping"))

    logger.info(response.bodyString())
}
