package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.weekNb
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class WeekOfYearTest {

    @Test
    fun `should get days of week`() {
        2022.weekNb(38).days shouldBe listOf(
            LocalDate.parse("2022-09-19"),
            LocalDate.parse("2022-09-20"),
            LocalDate.parse("2022-09-21"),
            LocalDate.parse("2022-09-22"),
            LocalDate.parse("2022-09-23"),
            LocalDate.parse("2022-09-24"),
            LocalDate.parse("2022-09-25")
        )

        2022.weekNb(0).days shouldHaveSize 2
        2022.weekNb(52).days shouldHaveSize 6
    }
}