package io.jterrier.fiprecorder

import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class FipRecorderTest {

    @Test @Disabled
    fun `Ping test`() {
        assertEquals(app(Request(GET, "/ping")), Response(OK).body("pong"))
    }

}
