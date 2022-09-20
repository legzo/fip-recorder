package io.jterrier.fiprecorder.database.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object Songs : IntIdTable() {
    val artist = varchar("artist", 200)
    val title = varchar("title", 200)
    val year = integer("year")
    val playedAt = date("played_at")
}