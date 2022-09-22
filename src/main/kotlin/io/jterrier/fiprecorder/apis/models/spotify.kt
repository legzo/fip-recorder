package io.jterrier.fiprecorder.apis.models

data class SpotifyPlaylistList(
    val items: List<SpotifyPlaylist>
)

data class SpotifyPlaylist(
    val id: String,
    val name: String,
    val href: String,
    val tracks: SpotifyTracks
)

data class SpotifyTracks(
    val total: Int
)

data class SpotifyPlaylistCreation(
    val name: String,
    val description: String,
    val public: Boolean = true,
    val collaborative: Boolean = false
)