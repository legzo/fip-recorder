package io.jterrier.fiprecorder.apis.models

data class SongList(
    val songs: List<Song>,
    val next: String?
)

data class Song(
    val firstLine: String,
    val secondLine: String,
    val thirdLine: String?
)
