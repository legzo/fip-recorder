package io.jterrier.fiprecorder.services

import io.jterrier.fiprecorder.models.Track
import java.time.LocalDate

interface PlayedTracksRepository {

    fun getPlayedTracksForDate(localDate: LocalDate): List<Track>

}