package io.jterrier.fiprecorder

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

private val zoneId = "Europe/Paris"

fun LocalDate.toEpoch() = atStartOfDay(ZoneId.of(zoneId)).toEpochSecond()
fun fromEpoch(epoch: Long): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch * 1000), ZoneId.systemDefault())
