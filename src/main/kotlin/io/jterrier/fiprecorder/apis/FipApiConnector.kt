package io.jterrier.fiprecorder.apis

import io.jterrier.fiprecorder.apis.models.FipSong
import io.jterrier.fiprecorder.apis.models.FipSongList
import io.jterrier.fiprecorder.fromEpoch
import io.jterrier.fiprecorder.models.Track
import io.jterrier.fiprecorder.services.PlayedTracksRepository
import org.http4k.client.OkHttp
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.format.Jackson.auto
import org.slf4j.LoggerFactory
import io.jterrier.fiprecorder.toEpoch
import java.time.LocalDate

class FipApiConnector : PlayedTracksRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val apiUrl = "https://www.radiofrance.fr/api/v1.9/stations/fip/webradios/fip/songs"

    private val client: HttpHandler = OkHttp()
    private val fipSongListLens = Body.auto<FipSongList>().toLens()

    override fun getPlayedTracksForDate(localDate: LocalDate): List<Track> =
        getSongWithCursor(epoch = localDate.toEpoch(), cursor = null)
            .map { it.toTrack() }

    private fun getSongWithCursor(
        epoch: Long,
        cursor: String?,
        currentSongList: List<FipSong> = listOf()
    ): List<FipSong> {
        logger.info("Request to Fip with cursor=$cursor")

        val response: Response = client(
            Request(Method.GET, apiUrl)
                .query("timestamp", epoch.toString())
                .query("limit", "100")
                .query("pageCursor", cursor ?: "")
        )

        val songList = fipSongListLens(response)

        // Le curseur dépasse la durée de la journée, du coup on break dès qu'on
        // atteint une chanson ayant été diffusée le jour suivant.
        val (sameDaySongs, nextDaySongs) = songList.songs.partition { it.start <= epoch + 24 * 60 * 60 }

        return when {
            songList.next == null || nextDaySongs.isNotEmpty() -> currentSongList + sameDaySongs
            else -> getSongWithCursor(epoch, songList.next, currentSongList + sameDaySongs)
        }
    }

    private fun FipSong.toTrack() =
        Track(
            title = this.firstLine,
            artist = this.secondLine,
            album = this.release.title.orEmpty(),
            label = this.release.label.orEmpty(),
            year = this.thirdLine?.toInt(),
            visualUrl = this.visual?.src.orEmpty(),
            durationInSeconds = (this.end - this.start).toInt(),
            startTime = fromEpoch(this.start),
            endTime = fromEpoch(this.end),
            spotifyId = this.links
                .firstOrNull { it.label == "spotify" }?.url
                .orEmpty()
                .removePrefix("https://open.spotify.com/track/")
        )

}