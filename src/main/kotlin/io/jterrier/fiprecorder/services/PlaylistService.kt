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
        val toPlaylistName = week.toPlaylistName()
        if (playlistExists(week).not()) {
            spotifyApi.createPlaylist(name = toPlaylistName, trackUris = trackUris)
        } else {
            logger.info("Playlist $toPlaylistName already exists")
        }
    }

    fun playlistExists(week: WeekOfYear): Boolean =
        spotifyApi
            .getPlaylists()
            .any { it.name == week.toPlaylistName() }


    private fun WeekOfYear.toPlaylistName() =
        "❤️ Fip $year week#$weekIndex"

}