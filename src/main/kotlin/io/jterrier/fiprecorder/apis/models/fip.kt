package io.jterrier.fiprecorder.apis.models

import io.jterrier.fiprecorder.fromEpoch

data class SongList(
    val songs: List<Song>,
    val next: String?
)

data class Song(
    val firstLine: String,
    val secondLine: String,
    val thirdLine: String?,
    val start: Long = 0,
    val end: Long = 0
) {
    val durationInSeconds = end - start

    val startAsTime = fromEpoch(start)
    val endAsTime = fromEpoch(end)
}
