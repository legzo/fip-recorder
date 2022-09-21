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
import io.jterrier.fiprecorder.services.TracksStorageRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.batchInsert
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
            .map {
                Track(
                    title = it[title],
                    artist = it[artist],
                    album = it[album],
                    label = it[label],
                    year = it[year],
                    visualUrl = it[visualUrl],
                    durationInSeconds = it[durationInSeconds],
                    startTime = it[startTime],
                    endTime = it[endTime],
                    spotifyId = it[spotifyId],
                )
            }
    }

    override fun isDateDone(date: LocalDate): Boolean = transaction {
        TracksTable
            .select { playedDate eq date }.empty().not()
    }

}
