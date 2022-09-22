package io.jterrier.fiprecorder.controllers

import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.lens.localDate

val dateQuery = Query.localDate().required("date")
val yearQuery = Query.int().required("year")
val weekQuery = Query.int().required("week")