package io.jterrier.fiprecorder.apis

import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.valueOrNull
import io.jterrier.fiprecorder.apis.models.SpotifyPlaylist
import io.jterrier.fiprecorder.apis.models.SpotifyPlaylistCreation
import io.jterrier.fiprecorder.apis.models.SpotifyPlaylistList
import io.jterrier.fiprecorder.models.Playlist
import io.jterrier.fiprecorder.services.PlaylistRepository
import io.jterrier.fiprecorder.spotifyConfig
import org.http4k.client.OkHttp
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ClientFilters
import org.http4k.format.Jackson.auto
import org.http4k.lens.WebForm
import org.http4k.security.ContentTypeJsonOrForm
import org.http4k.security.CredentialsProvider
import org.http4k.security.ExpiringCredentials
import org.http4k.security.OAuthWebForms
import org.http4k.security.OAuthWebForms.clientId
import org.http4k.security.OAuthWebForms.clientSecret
import org.http4k.security.OAuthWebForms.grantType
import org.http4k.security.OAuthWebForms.redirectUri
import org.http4k.security.OAuthWebForms.refreshToken
import org.http4k.security.RefreshCredentials
import org.http4k.security.Refreshing
import org.http4k.security.oauth.core.RefreshToken
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Instant

class SpotifyApiConnector : PlaylistRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val apiAuthUrl = "https://accounts.spotify.com/api/token"
    private val apiUrl = "https://api.spotify.com/v1"

    private val client: HttpHandler = OkHttp()
    private val playlistListLens = Body.auto<SpotifyPlaylistList>().toLens()
    private val playlistLens = Body.auto<SpotifyPlaylist>().toLens()
    private val playlistCreationLens = Body.auto<SpotifyPlaylistCreation>().toLens()
    private val playlistUpdateLens = Body.auto<List<String>>().toLens()

    private val refreshTokenFn = RefreshCredentials<String> { oldToken ->

        val tokenToSend = oldToken?.let { RefreshToken(it) } ?: RefreshToken(spotifyConfig.refreshToken.value)

        val clientAuth = ClientFilters.BasicAuth(spotifyConfig.clientId.value, spotifyConfig.clientSecret.value)
            .then(client)

        clientAuth(Request(POST, apiAuthUrl)
            .with(
            OAuthWebForms.requestForm of WebForm()
                .with(
                    grantType of "refresh_token",
                    redirectUri of Uri.of("https://localhost8888/callback"),
                    clientId of spotifyConfig.clientId.value,
                    clientSecret of spotifyConfig.clientSecret.value,
                    refreshToken of tokenToSend
                )
                )).takeIf { it.status.successful }
            ?.let { ContentTypeJsonOrForm()(it).map { tokenDetails -> tokenDetails.accessToken }.valueOrNull() }
            ?.let { accessToken ->
                ExpiringCredentials(
                    accessToken.value,
                    accessToken.expiresIn?.let { Clock.systemUTC().instant().plusSeconds(it) } ?: Instant.MAX
                )
            }
    }

    private val refreshingTokenClient =
        ClientFilters.BearerAuth(
            CredentialsProvider.Refreshing(
                refreshFn = refreshTokenFn
            )
        ).then(client)

    override fun getPlaylists(): List<Playlist> {
        logger.info("Getting playlists")

        val response =
            refreshingTokenClient(Request(GET, "$apiUrl/users/legzo/playlists"))

        val spotifyPlaylists = playlistListLens(response).items
        logger.info("Got ${spotifyPlaylists.size} playlists")
        return spotifyPlaylists.map { it.toPlaylist() }
    }

    override fun createPlaylist(name: String, trackUris: List<String>): Playlist {
        logger.info("Creating playlist $name")

        val createPlaylist = Request(POST, "$apiUrl/users/legzo/playlists")

        val body = SpotifyPlaylistCreation(
            name = name,
            description = "Playlist autogénérée 🤖"
        )

        val response =
            refreshingTokenClient(playlistCreationLens.inject(body, createPlaylist))

        val createdPlaylist = playlistLens(response).toPlaylist()

        val addTracksToPlaylistRequest = Request(POST, "$apiUrl/users/legzo/playlists/${createdPlaylist.id}/tracks")

        val playlistUpdateResponse = refreshingTokenClient(playlistUpdateLens.inject(trackUris, addTracksToPlaylistRequest))

        logger.info("Update response $playlistUpdateResponse")

        return createdPlaylist
    }

    private fun SpotifyPlaylist.toPlaylist() =
        Playlist(
            id = this.id,
            name = this.name,
            url = this.href,
            tracksCount = this.tracks.total
        )

}
