package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.DateStatistics
import io.jterrier.fiprecorder.models.Track
import java.time.LocalDate

class TrackService(
    private val fipApi: PlayedTracksRepository,
    private val database: TracksStorageRepository,
) {

    fun getTracksForDate(
        date: LocalDate,
    ): List<Track> {
        val dateDone = database.isDateDone(date)

        return if (dateDone) {
            database.getTracksForDate(date)
        } else {
            fipApi.getPlayedTracksForDate(date)
                .also { database.insertTracks(it) }
        }
    }

    fun getStatisticsForTracks(tracks: Collection<Track>) =
        DateStatistics(
            trackCount = tracks.size,
            topTenLabels = tracks
                .groupingBy { it.label }
                .eachCount()
                .toList()
                .filter { (label, _) -> label.isNotBlank() }
                .sortedByDescending { (_, count) -> count }
                .take(10)
                .toMap(),
            topTenYears = tracks
                .groupingBy { it.year ?: -1 }
                .eachCount()
                .toList()
                .filter { (year, _) -> year != -1 }
                .sortedByDescending { (_, count) -> count }
                .take(10)
                .toMap(),
        )

}