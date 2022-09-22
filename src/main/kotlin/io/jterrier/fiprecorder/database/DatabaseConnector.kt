package io.jterrier.fiprecorder.database

import io.jterrier.fiprecorder.database.models.TracksTable
import io.jterrier.fiprecorder.database.models.TracksTable.album
import io.jterrier.fiprecorder.database.models.TracksTable.artist
import io.jterrier.fiprecorder.database.models.TracksTable.durationInSeconds
import io.jterrier.fiprecorder.database.models.TracksTable.endTime
import io.jterrier.fiprecorder.database.models.TracksTable.label
import io.jterrier.fiprecorder.database.models.TracksTable.playedDate
import io.jterrier.fiprecorder.database.models.TracksTable.spotifyId
import io.jterrier.fiprecorder.database.models.TracksTable.startTime
import io.jterrier.fiprecorder.database.models.TracksTable.title
import io.jterrier.fiprecorder.database.models.TracksTable.visualUrl
import io.jterrier.fiprecorder.database.models.TracksTable.year
import io.jterrier.fiprecorder.databaseConfig
import io.jterrier.fiprecorder.models.Track
import io.jterrier.fiprecorder.models.WeekOfYear
import io.jterrier.fiprecorder.services.TracksStorageRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class DatabaseConnector : TracksStorageRepository {

    init {
        Database.connect(
            url = databaseConfig.url.value,
            driver = "org.postgresql.Driver",
            user = databaseConfig.user.value,
            password = databaseConfig.password.value
        )

        transaction {
            // print sql to std-out
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(TracksTable)
        }
    }

    override fun insertTracks(tracks: List<Track>) = transaction {
        TracksTable
            .batchInsert(tracks, shouldReturnGeneratedValues = false) { track ->
                this[title] = track.title
                this[artist] = track.artist
                this[album] = track.album
                this[label] = track.label
                this[year] = track.year
                this[visualUrl] = track.visualUrl
                this[durationInSeconds] = track.durationInSeconds
                this[startTime] = track.startTime
                this[endTime] = track.endTime
                this[playedDate] = track.startTime.toLocalDate()
                this[spotifyId] = track.spotifyId
            }
        Unit
    }

    override fun getTracksForDate(localDate: LocalDate): List<Track> = transaction {
        TracksTable
            .select { playedDate eq localDate }
            .map { it.toTrackModel() }
    }


    override fun getTracksForWeek(week: WeekOfYear): List<Track> = transaction {
        TracksTable
            .select { playedDate inList week.days }
            .map { it.toTrackModel() }
    }

    private fun ResultRow.toTrackModel() = Track(
        title = this[title],
        artist = this[artist],
        album = this[album],
        label = this[label],
        year = this[year],
        visualUrl = this[visualUrl],
        durationInSeconds = this[durationInSeconds],
        startTime = this[startTime],
        endTime = this[endTime],
        spotifyId = this[spotifyId],
    )

    override fun clearTracksForDate(localDate: LocalDate) = transaction {
        TracksTable
            .deleteWhere { playedDate eq localDate }
        Unit
    }

    override fun clearTracksForWeek(week: WeekOfYear) =
        week
            .days
            .forEach { clearTracksForDate(it) }

    override fun isDateDone(date: LocalDate): Boolean = transaction {
        TracksTable
            .select { playedDate eq date }
            .count() > 200
    }

    override fun isWeekDone(week: WeekOfYear): Boolean =
        week
            .days
            .all { isDateDone(it) }

}
