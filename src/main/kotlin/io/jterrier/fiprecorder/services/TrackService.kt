package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.Track
import java.time.LocalDate

class TrackService(
    private val fipApi: PlayedTracksRepository,
    private val database: TracksStorageRepository,
) {

    fun getTracksForDate(date: LocalDate): List<Track> =
        when {
            database.isDateDone(date) -> database.getTracksForDate(date)
            else -> fipApi.getPlayedTracksForDate(date)
                .also { database.insertTracks(it) }
        }

}