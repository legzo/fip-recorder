package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.Statistics
import io.jterrier.fiprecorder.models.Track
import java.time.LocalDateTime

class StatsService {

    fun getTopTracks(tracks: Collection<Track>, itemNb: Int): Map<Track, Int> =
        tracks
            .map { it.copy(startTime = LocalDateTime.MIN, endTime = LocalDateTime.MIN) }
            .filter { it.spotifyId.isNotBlank() }
            .groupAndSortByCount(itemNbForTops = itemNb) { it }


    fun getStatisticsForTracks(tracks: Collection<Track>, itemNbForTops: Int) =
        Statistics(
            trackCount = tracks.size,
            topLabels = tracks.groupAndSortByCount(
                itemNbForTops = itemNbForTops,
                filtering = { (label, _) -> label.isNotBlank() }
            ) { it.label },
            topYears = tracks.groupAndSortByCount(
                itemNbForTops = itemNbForTops,
                filtering = { (year, _) -> year != -1 }
            ) { it.year ?: -1 }
        )

    private fun <T> Collection<Track>.groupAndSortByCount(
        itemNbForTops: Int,
        filtering: (Pair<T, Int>) -> Boolean = { true },
        groupingFunction: (Track) -> T
    ) = groupingBy(groupingFunction)
        .eachCount()
        .toList()
        .filter(filtering)
        .sortedByDescending { (_, count) -> count }
        .take(itemNbForTops)
        .toMap()
}