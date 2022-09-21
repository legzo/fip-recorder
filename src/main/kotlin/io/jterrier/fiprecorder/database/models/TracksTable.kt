package io.jterrier.fiprecorder.database.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object TracksTable : IntIdTable() {

    // TODO tuner les varchar avec des données collectées de l'API FIP

    val title = varchar("title", 200)
    val artist = varchar("artist", 200)
    val album = varchar("album", 200)
    val label = varchar("label", 200)
    val year = integer("year").nullable()
    val visualUrl = varchar("visual_url", 200)
    val durationInSeconds = integer("duration_in_s")
    val startTime = datetime("start_time")
    val endTime = datetime("end_time")
    val playedDate = date("played_date")
    val spotifyId = varchar("spotify_id", 100)
}