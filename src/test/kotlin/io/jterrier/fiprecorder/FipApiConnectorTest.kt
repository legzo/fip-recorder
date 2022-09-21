package io.jterrier.fiprecorder

import io.jterrier.fiprecorder.apis.FipApiConnector
import org.slf4j.LoggerFactory
import java.time.LocalDate


fun main() {
    val logger = LoggerFactory.getLogger("io.jterrier.fiprecorder.apis.FipApiConnector")
    logger.info("▶️")
    val songsOf17 = FipApiConnector().getSongsForDay(LocalDate.of(2022, 9, 18))
    logger.info("⏹")
    logger.info("Top 10 songs of 17 : ${songsOf17.take(10)}")
    logger.info("Nb songs of 17 : ${songsOf17.size}")
}
