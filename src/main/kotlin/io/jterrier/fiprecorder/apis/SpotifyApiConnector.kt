package io.jterrier.fiprecorder.apis

import io.jterrier.fiprecorder.apis.models.Playlist
import io.jterrier.fiprecorder.apis.models.PlaylistList
import io.jterrier.fiprecorder.spotifyConfig
import org.http4k.client.OkHttp
import org.http4k.core.Body
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.format.Jackson.auto
import org.http4k.security.oauth.client.RefreshingOAuthToken
import org.slf4j.LoggerFactory

class SpotifyApiConnector {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val apiAuthUrl = "https://accounts.spotify.com/api/token"
    private val apiUrl = "https://api.spotify.com/v1"

    private val client: HttpHandler = OkHttp()

    private val songListLens = Body.auto<PlaylistList>().toLens()

    private val clientCredentials = with(spotifyConfig) {
        Credentials(clientId.value, clientSecret.value)
    }

    private val refreshingOAuthClient =
        ClientFilters.RefreshingOAuthToken(
            oauthCredentials = clientCredentials,
            tokenUri = Uri.of(apiAuthUrl),
            backend = client,
        ).then(client)


    fun getPlaylists(): List<Playlist> {
        logger.info("Getting playlists")

        val response = refreshingOAuthClient(
            Request(GET, "$apiUrl/users/legzo/playlists")
        )

        val playlists = songListLens(response).items

        logger.info("Got ${playlists.size} playlists")
        return playlists
    }

}