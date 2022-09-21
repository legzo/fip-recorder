package io.jterrier.fiprecorder.database

import io.jterrier.fiprecorder.apis.models.Song
import io.jterrier.fiprecorder.database.models.SongsTable
import io.jterrier.fiprecorder.database.models.SongsTable.artist
import io.jterrier.fiprecorder.database.models.SongsTable.playedAt
import io.jterrier.fiprecorder.database.models.SongsTable.title
import io.jterrier.fiprecorder.database.models.SongsTable.year
import io.jterrier.fiprecorder.databaseConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class DatabaseConnector {

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
            SchemaUtils.create(SongsTable)
        }
    }

    fun insertSongs(songs: List<Song>, localDate: LocalDate) = transaction {
        SongsTable.batchInsert(songs, shouldReturnGeneratedValues = false) { song ->
            this[title] = song.firstLine
            this[artist] = song.secondLine
            this[year] = song.thirdLine?.toInt() ?: -1
            this[playedAt] = localDate
        }
    }

    fun isDateDone(date: LocalDate): Boolean = transaction {
        SongsTable.select { playedAt eq date }.empty().not()
    }

}
