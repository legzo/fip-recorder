package io.jterrier.fiprecorder.database

import dbPassword
import dbUrl
import dbUser
import io.jterrier.fiprecorder.apis.models.Song
import io.jterrier.fiprecorder.database.models.Songs
import io.jterrier.fiprecorder.database.models.Songs.artist
import io.jterrier.fiprecorder.database.models.Songs.playedAt
import io.jterrier.fiprecorder.database.models.Songs.title
import io.jterrier.fiprecorder.database.models.Songs.year
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
            url = dbUrl.value,
            driver = "org.postgresql.Driver",
            user = dbUser.value,
            password = dbPassword.value
        )

        transaction {
            // print sql to std-out
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Songs)
        }
    }

    fun insertSongs(songs: List<Song>, localDate: LocalDate) = transaction {
        Songs.batchInsert(songs, shouldReturnGeneratedValues = false) { song ->
            this[title] = song.firstLine
            this[artist] = song.secondLine
            this[year] = song.thirdLine?.toInt() ?: -1
            this[playedAt] = localDate
        }
    }

    fun isDateDone(date: LocalDate): Boolean = transaction {
        Songs.select { playedAt eq date }.empty().not()
    }

}
