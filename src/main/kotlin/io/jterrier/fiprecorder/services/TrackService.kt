package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.Track
import io.jterrier.fiprecorder.models.WeekOfYear
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class TrackService(
    private val fipApi: PlayedTracksRepository,
    private val database: TracksStorageRepository,
) {

    fun getTracksForDate(date: LocalDate): List<Track> =
        when {
            database.isDateDone(date) -> database.getTracksForDate(date)
            else -> fipApi.getPlayedTracksForDate(date)
                .also {
                    database.clearTracksForDate(date)
                    database.insertTracks(it)
                }
        }

    fun getTracksForWeek(week: WeekOfYear): List<Track> =
        when {
            database.isWeekDone(week) -> database.getTracksForWeek(week)
            else -> {
                runBlocking(Dispatchers.IO) {
                    week.days
                        .map { async { getTracksForDate(it) } }
                        .awaitAll()
                        .flatten()
                }
                    .also {
                        database.clearTracksForWeek(week)
                        database.insertTracks(it)
                    }
            }
        }

}