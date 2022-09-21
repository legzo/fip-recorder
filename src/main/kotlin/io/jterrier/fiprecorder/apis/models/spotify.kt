package io.jterrier.fiprecorder.apis.models

data class PlaylistList(
    val items: List<Playlist>
)

data class Playlist(
    val name: String,
    val href: String,
    val tracks: Tracks
)

data class Tracks(
    val total: Int
)