package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.DateStatistics
import io.jterrier.fiprecorder.models.Track

class StatsService {

    fun getStatisticsForTracks(tracks: Collection<Track>, itemNbForTops: Int) =
        DateStatistics(
            trackCount = tracks.size,
            topLabels = tracks
                .groupingBy { it.label }
                .eachCount()
                .toList()
                .filter { (label, _) -> label.isNotBlank() }
                .sortedByDescending { (_, count) -> count }
                .take(itemNbForTops)
                .toMap(),
            topYears = tracks
                .groupingBy { it.year ?: -1 }
                .eachCount()
                .toList()
                .filter { (year, _) -> year != -1 }
                .sortedByDescending { (_, count) -> count }
                .take(itemNbForTops)
                .toMap(),
        )
}