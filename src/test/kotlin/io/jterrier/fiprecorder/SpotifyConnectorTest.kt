package io.jterrier.fiprecorder

import io.jterrier.fiprecorder.apis.SpotifyApiConnector

fun main() {
    val api = SpotifyApiConnector()

    api.getPlaylists()
}

