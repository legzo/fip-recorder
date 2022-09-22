package io.jterrier.fiprecorder.controllers.web.models

import io.jterrier.fiprecorder.models.Statistics
import io.jterrier.fiprecorder.models.Track
import io.jterrier.fiprecorder.models.WeekOfYear
import org.http4k.template.ViewModel

data class WeekViewModel(
    val displayDate: String,
    val tracks: List<TrackAndCounterViewModel>,
    val stats: DateStatisticsViewModel,
    val links: LinksViewModel,
) : ViewModel {

    companion object {
        fun from(
            week: WeekOfYear,
            tracks: Map<Track, Int>,
            statisticsForDate: Statistics
        ) =
            WeekViewModel(
                displayDate = "${week.year} - week #${week.weekIndex}",
                tracks = tracks.map { (track, count) -> track.toTrackAndCounterViewModel(count) },
                stats = statisticsForDate.toViewModel(),
                links = LinksViewModel(
                    previous = "tracksOfWeek?year=${week.year}&week=${week.weekIndex - 1}",
                    next = "tracksOfWeek?year=${week.year}&week=${week.weekIndex + 1}",
                    createPlaylist = "api/playlists/create?year=${week.year}&week=${week.weekIndex}"
                )
            )

        private fun Track.toTrackAndCounterViewModel(count: Int) =
            TrackAndCounterViewModel(
                title = title,
                artist = artist,
                count = count
            )

    }
}
