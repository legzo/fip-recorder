package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.Track
import io.jterrier.fiprecorder.models.WeekOfYear
import java.time.LocalDate

interface TracksStorageRepository {

    fun insertTracks(tracks: List<Track>)

    fun getTracksForDate(localDate: LocalDate): List<Track>
    fun getTracksForWeek(week: WeekOfYear): List<Track>

    fun clearTracksForDate(localDate: LocalDate)
    fun clearTracksForWeek(week: WeekOfYear)

    fun isDateDone(date: LocalDate): Boolean
    fun isWeekDone(week: WeekOfYear): Boolean

}