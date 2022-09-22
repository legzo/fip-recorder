package io.jterrier.fiprecorder.models

import java.time.LocalDate
import java.time.Year
import java.time.temporal.WeekFields
import java.util.Locale

data class WeekOfYear(
    val year: Int,
    val weekIndex: Int,
) {
    val days = getDaysForWeekOfYear(this)

    companion object {
        private val weekFields = WeekFields.of(Locale.getDefault())

        internal fun getDaysForWeekOfYear(week: WeekOfYear): List<LocalDate> {
            val year = Year.of(week.year)

            return (1..year.length())
                .map { year.atDay(it) }
                .filter { it.get(weekFields.weekOfYear()) == week.weekIndex }
        }

    }

}

fun Int.weekNb(index: Int) =
    WeekOfYear(
        year = this,
        weekIndex = index
    )
