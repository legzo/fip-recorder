package io.jterrier.fiprecorder.web

import io.jterrier.fiprecorder.web.models.HandlebarsViewModel
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel

class WebController {

    fun showPage(request: Request): Response {
        val renderer = HandlebarsTemplates().CachingClasspath()
        val view = Body.viewModel(renderer, ContentType.TEXT_HTML).toLens()
        val viewModel = HandlebarsViewModel("Hello there!")
        return Response(OK).with(view of viewModel)
    }

}