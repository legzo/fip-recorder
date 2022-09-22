package io.jterrier.fiprecorder.controllers.web.models

import io.jterrier.fiprecorder.models.Statistics
import java.util.Locale

data class TrackViewModel(
    val title: String,
    val artist: String,
    val playedAt: String
)

data class TrackAndCounterViewModel(
    val title: String,
    val artist: String,
    val count: Int
)

data class DateStatisticsViewModel(
    val trackCount: Int,
    val topLabels: Map<String, Int>,
    val topYears: Map<Int, Int>
)

data class LinksViewModel(
    val previous: String,
    val next: String,
    val createPlaylist: String = "",
)


internal fun Statistics.toViewModel() =
    DateStatisticsViewModel(
        trackCount = trackCount,
        topLabels = topLabels.mapKeys { (label, _) -> label.capitalize() },
        topYears = topYears,
    )

internal fun String.capitalize() =
    lowercase(Locale.getDefault())
        .replaceFirstChar { it.titlecase(Locale.getDefault()) }