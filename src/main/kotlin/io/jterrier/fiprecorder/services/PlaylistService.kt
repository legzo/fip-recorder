package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.Playlist

class PlaylistService(
    private val spotifyApi: PlaylistRepository
) {

    fun getPlaylists(): List<Playlist> =
        spotifyApi.getPlaylists()

}