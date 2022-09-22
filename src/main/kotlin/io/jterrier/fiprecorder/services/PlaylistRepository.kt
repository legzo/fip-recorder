package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.Playlist

interface PlaylistRepository {

    fun getPlaylists(): List<Playlist>
    fun createPlaylist(name: String, trackUris: List<String>): Playlist

}
