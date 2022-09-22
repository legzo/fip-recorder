package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.DateStatistics
import io.jterrier.fiprecorder.models.Track

class StatsService {

    fun getStatisticsForTracks(tracks: Collection<Track>, itemNbForTops: Int) =
        DateStatistics(
            trackCount = tracks.size,
            topLabels = tracks.groupAndSortByCount(
                itemNbForTops = itemNbForTops,
                filtering = { (label, _) -> label.isNotBlank() }
            ) { it.label },
            topYears = tracks.groupAndSortByCount(
                itemNbForTops = itemNbForTops,
                filtering = { (year, _) -> year != -1 }
            ) { it.year ?: -1 },
        )

    private fun <T> Collection<Track>.groupAndSortByCount(
        itemNbForTops: Int,
        filtering: (Pair<T, Int>) -> Boolean,
        groupingFunction: (Track) -> T
    ) = groupingBy(groupingFunction)
        .eachCount()
        .toList()
        .filter(filtering)
        .sortedByDescending { (_, count) -> count }
        .take(itemNbForTops)
        .toMap()
}