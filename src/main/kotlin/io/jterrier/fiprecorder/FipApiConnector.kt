package io.jterrier.fiprecorder

import org.http4k.client.OkHttp
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.format.Jackson.auto
import java.time.LocalDate
import java.time.ZoneId


data class SongList(
    val songs: List<Song>,
    val next: String?
)

data class Song(
    val firstLine: String,
    val secondLine: String,
    val thirdLine: String?
)


class FipApiConnector {

    private val apiUrl = "https://www.radiofrance.fr/api/v1.9/stations/fip/webradios/fip/songs"

    private val client: HttpHandler = OkHttp()
    private val songListLens = Body.auto<SongList>().toLens()

    fun getSongsForDay(date: LocalDate): List<Song> =
        getSongWithCursor(epoch = date.toEpoch(), cursor = null)

    private fun LocalDate.toEpoch() = atStartOfDay(ZoneId.of("Europe/Paris")).toEpochSecond()

    private fun getSongWithCursor(
        epoch: Long,
        cursor: String?,
        currentSongList: List<Song> = listOf()
    ): List<Song> {
        val response: Response = client(
            Request(Method.GET, apiUrl)
                .query("timestamp", epoch.toString())
                .query("limit", "100")
                .query("pageCursor", cursor ?: "")
        )

        val songList = songListLens(response)

        return when (songList.next) {
            null -> currentSongList + songList.songs
            else -> getSongWithCursor(epoch, songList.next, currentSongList + songList.songs)
        }
    }
}