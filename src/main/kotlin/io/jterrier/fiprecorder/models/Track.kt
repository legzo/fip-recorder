package io.jterrier.fiprecorder.models

import java.time.LocalDateTime

data class Track(
    val title: String,
    val artist: String,
    val album: String,
    val label: String,
    val year: Int?,
    val visualUrl: String,
    val durationInSeconds: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val spotifyId: String,
) {
    val spotifyUrl = "http://open.spotify.com/track/$spotifyId"
    val spotifyUri = "spotify:track:$spotifyId"
}
