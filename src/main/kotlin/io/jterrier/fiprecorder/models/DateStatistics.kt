package io.jterrier.fiprecorder.models

data class DateStatistics(
    val trackCount: Int,
    val topTenLabels: Map<String, Int>,
    val topTenYears: Map<Int, Int>,
)
