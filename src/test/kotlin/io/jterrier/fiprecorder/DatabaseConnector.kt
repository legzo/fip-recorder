package io.jterrier.fiprecorder

import io.jterrier.fiprecorder.apis.models.Song
import io.jterrier.fiprecorder.database.DatabaseConnector
import org.slf4j.LoggerFactory
import java.time.LocalDate

fun main() {
    val logger = LoggerFactory.getLogger("io.jterrier.fiprecorder.database.DatabaseConnectorTest")

    val db = DatabaseConnector()

    val now = LocalDate.now()

    logger.info("db.isDateDone: ${db.isDateDone(now)}")

    db.insertSongs(
        listOf(
            Song(
                firstLine = "test",
                secondLine = "sds",
                thirdLine = "2012",

                )
        ), LocalDate.now()
    )

    logger.info("db.isDateDone: ${db.isDateDone(now)}")
}

