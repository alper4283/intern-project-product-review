package com.alper.productreview.ui.util

import java.util.Locale
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val reviewDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        .withZone(ZoneId.systemDefault())

fun formatIsoTimestamp(iso: String): String {
    return try {
        reviewDateFormatter.format(Instant.parse(iso))
    } catch (_: Exception) {
        iso // fallback if parsing fails
    }
}

fun formatRating(rating: Double): String =
    String.format(Locale.US, "%.2f", rating)
