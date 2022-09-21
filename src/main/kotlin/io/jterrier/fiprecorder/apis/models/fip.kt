package io.jterrier.fiprecorder.apis.models

data class FipSongList(
    val songs: List<FipSong>,
    val next: String?,
)

data class FipSong(
    val firstLine: String,
    val secondLine: String,
    val thirdLine: String?,
    val release: FipRelease,
    val visual: FipVisual?,
    val links: Set<FipLink>,
    val start: Long,
    val end: Long,
)

data class FipRelease(
    val title: String?,
    val label: String?,
)

data class FipVisual(
    val src: String,
)

data class FipLink(
    val label: String,
    val url: String,
)