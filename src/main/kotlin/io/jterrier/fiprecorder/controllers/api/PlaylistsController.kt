package io.jterrier.fiprecorder.controllers.api

import io.jterrier.fiprecorder.controllers.weekQuery
import io.jterrier.fiprecorder.controllers.yearQuery
import io.jterrier.fiprecorder.models.Playlist
import io.jterrier.fiprecorder.models.weekNb
import io.jterrier.fiprecorder.services.PlaylistService
import io.jterrier.fiprecorder.services.StatsService
import io.jterrier.fiprecorder.services.TrackService
import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.slf4j.LoggerFactory

class PlaylistsController(
    private val playlistService: PlaylistService,
    private val trackService: TrackService,
    private val statsService: StatsService,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val playlistListLens = Body.auto<List<Playlist>>().toLens()

    fun getPlaylists(@Suppress("UNUSED_PARAMETER") request: Request) =
        playlistListLens.inject(playlistService.getPlaylists(), Response(OK))
            .also { logger.info("Returning playlists") }

    fun publishPlaylist(request: Request): Response {
        val year = yearQuery(request)
        val weekIndex = weekQuery(request)

        logger.info("Treating week : $year-$weekIndex")

        val week = year.weekNb(weekIndex)

        return when (playlistService.playlistExists(week)) {
            true -> {
                logger.info("Playlist already exists, doing nothing")
                Response(NO_CONTENT).andRefresh()
            }
            false -> {
                val tracks = trackService.getTracksForWeek(week)
                val topTracksIds = statsService
                    .getTopTracks(tracks, 30)
                    .map { it.key.spotifyUri }

                playlistService.publishPlaylist(week, topTracksIds)
                Response(OK).andRefresh()
            }
        }
    }

    private fun Response.andRefresh() =
        header("HX-Refresh", "true")

}