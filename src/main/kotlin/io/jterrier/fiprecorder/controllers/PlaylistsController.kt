package io.jterrier.fiprecorder.controllers

import io.jterrier.fiprecorder.apis.SpotifyApiConnector
import io.jterrier.fiprecorder.apis.models.Playlist
import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.slf4j.LoggerFactory

class PlaylistsController(
    private val spotifyApiConnector: SpotifyApiConnector
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val playlistListLens = Body.auto<List<Playlist>>().toLens()

    fun getPlaylists(request: Request) =
        playlistListLens.inject(spotifyApiConnector.getPlaylists(), Response(Status.OK))

}