package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.Playlist
import io.jterrier.fiprecorder.models.WeekOfYear
import org.slf4j.LoggerFactory

class PlaylistService(
    private val spotifyApi: PlaylistRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getPlaylists(): List<Playlist> =
        spotifyApi.getPlaylists()

    fun publishPlaylist(week: WeekOfYear, trackUris: List<String>) {
        val existingPlaylists = spotifyApi.getPlaylists()
        val toPlaylistName = week.toPlaylistName()

        val playlistAlreadyExistsForWeek = existingPlaylists.any { it.name == toPlaylistName }
        if (playlistAlreadyExistsForWeek.not()) {
            spotifyApi.createPlaylist(name = toPlaylistName, trackUris = trackUris)
        } else {
            logger.info("Playlist $toPlaylistName already exists")
        }
    }

    private fun WeekOfYear.toPlaylistName() =
        "❤️ Fip $year week#$weekIndex"

}