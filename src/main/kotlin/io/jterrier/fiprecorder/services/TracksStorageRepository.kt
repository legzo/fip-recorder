package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.Track
import java.time.LocalDate

interface TracksStorageRepository {

    fun insertTracks(tracks: List<Track>)

    fun getTracksForDate(localDate: LocalDate): List<Track>

    fun isDateDone(date: LocalDate): Boolean

}