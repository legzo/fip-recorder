package io.jterrier.fiprecorder.apis

import io.jterrier.fiprecorder.apis.models.Song
import io.jterrier.fiprecorder.apis.models.SongList
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

class FipApiConnector {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val apiUrl = "https://www.radiofrance.fr/api/v1.9/stations/fip/webradios/fip/songs"

    private val client: HttpHandler = OkHttp()
    private val songListLens = Body.auto<SongList>().toLens()

    fun getSongsForDay(date: LocalDate): List<Song> =
        getSongWithCursor(epoch = date.toEpoch(), cursor = null)

    private fun getSongWithCursor(
        epoch: Long,
        cursor: String?,
        currentSongList: List<Song> = listOf()
    ): List<Song> {
        logger.info("Request to Fip with cursor=$cursor")

        val response: Response = client(
            Request(Method.GET, apiUrl)
                .query("timestamp", epoch.toString())
                .query("limit", "100")
                .query("pageCursor", cursor ?: "")
        )

        val songList = songListLens(response)

        // Le curseur dépasse la durée de la journée, du coup on break dès qu'on
        // atteint une chanson ayant été diffusée le jour suivant.
        val (sameDaySongs, nextDaySongs) = songList.songs.partition { it.start <= epoch + 24 * 60 * 60 }

        return when {
            songList.next == null || nextDaySongs.isNotEmpty() -> currentSongList + sameDaySongs
            else -> getSongWithCursor(epoch, songList.next, currentSongList + sameDaySongs)
        }
    }
}