package io.jterrier.fiprecorder.controllers.web.models

import io.jterrier.fiprecorder.models.Statistics
import io.jterrier.fiprecorder.models.Track
import org.http4k.template.ViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DayViewModel(
    val displayDate: String,
    val tracks: List<TrackViewModel>,
    val stats: DateStatisticsViewModel,
    val links: LinksViewModel,
) : ViewModel {

    companion object {
        fun from(
            localDate: LocalDate,
            tracks: List<Track>,
            statisticsForDate: Statistics
        ) =
            DayViewModel(
                displayDate = localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                tracks = tracks.map { it.toViewModel() },
                stats = statisticsForDate.toViewModel(),
                links = LinksViewModel(
                    previous = "tracks?date=" + localDate.minusDays(1),
                    next = "tracks?date=" + localDate.plusDays(1)
                )
            )

        private fun Track.toViewModel() =
            TrackViewModel(
                title = title,
                artist = artist,
                playedAt = startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            )

    }
}
