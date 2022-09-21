package io.jterrier.fiprecorder.apis.models

data class SpotifyPlaylistList(
    val items: List<SpotifyPlaylist>
)

data class SpotifyPlaylist(
    val name: String,
    val href: String,
    val tracks: SpotifyTracks
)

data class SpotifyTracks(
    val total: Int
)