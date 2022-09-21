package io.jterrier.fiprecorder.controllers.web.models

import io.jterrier.fiprecorder.models.Track
import org.http4k.template.ViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ListOfTracksViewModel(
    val displayDate: String,
    val tracks: List<TrackViewModel>,
    val count: Int,
) : ViewModel {

    companion object {
        fun from(localDate: LocalDate, tracks: List<Track>) =
            ListOfTracksViewModel(
                displayDate = localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                tracks = tracks.map { it.toViewModel() },
                count = tracks.size,
            )

        private fun Track.toViewModel() =
            TrackViewModel(
                title = title,
                artist = artist,
                playedAt = startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            )
    }

}
