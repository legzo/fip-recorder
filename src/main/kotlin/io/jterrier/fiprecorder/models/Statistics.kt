package io.jterrier.fiprecorder.models

data class Statistics(
    val trackCount: Int,
    val topLabels: Map<String, Int>,
    val topYears: Map<Int, Int>,
)
