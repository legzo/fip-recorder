package io.jterrier.fiprecorder.models

data class DateStatistics(
    val trackCount: Int,
    val topLabels: Map<String, Int>,
    val topYears: Map<Int, Int>,
)
