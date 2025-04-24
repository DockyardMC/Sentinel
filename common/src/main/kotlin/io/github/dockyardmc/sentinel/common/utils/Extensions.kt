package io.github.dockyardmc.sentinel.common.utils

import kotlinx.datetime.*
import kotlinx.datetime.format.char
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object Extensions {
    val customFormat = LocalDateTime.Format {
        dayOfMonth()
        char('/')
        monthNumber()
        char('/')
        year()
        char(' ')
        hour()
        char(':')
        minute()
    }
}

fun LocalDateTime.Companion.getNow(): LocalDateTime {
    val now = Clock.System.now()
    return now.toLocalDateTime(TimeZone.UTC)
}

fun LocalDateTime.formatted(): String {
    val inTime = formatRelativeTime(Clock.System.now(), this.toInstant(TimeZone.UTC))
    return "${Extensions.customFormat.format(this)} <gray>($inTime)"
}

fun formatRelativeTime(from: Instant, to: Instant): String {
    val duration = to - from

    val isPast = duration.isNegative()
    val absoluteDuration = duration.absoluteValue

    return when {
        absoluteDuration < 1.toDuration(DurationUnit.SECONDS) -> "just now"

        absoluteDuration < 1.toDuration(DurationUnit.MINUTES) ->
            if (isPast) "${absoluteDuration.inWholeSeconds} seconds ago" else "in ${absoluteDuration.inWholeSeconds} seconds"

        absoluteDuration < 1.toDuration(DurationUnit.HOURS) ->
            if (isPast) "${absoluteDuration.inWholeMinutes} minutes ago" else "in ${absoluteDuration.inWholeMinutes} minutes"

        absoluteDuration < 1.toDuration(DurationUnit.DAYS) ->
            if (isPast) "${absoluteDuration.inWholeHours} hours ago" else "in ${absoluteDuration.inWholeHours} hours"

        absoluteDuration < 30.toDuration(DurationUnit.DAYS) ->
            if (isPast) "${absoluteDuration.inWholeDays} days ago" else "in ${absoluteDuration.inWholeDays} days"

        absoluteDuration < 365.toDuration(DurationUnit.DAYS) ->
            if (isPast) "${absoluteDuration.inWholeDays / 30} months ago" else "in ${absoluteDuration.inWholeDays / 30} months"

        else ->
            if (isPast) "${absoluteDuration.inWholeDays / 365} years ago" else "in ${absoluteDuration.inWholeDays / 365} years"
    }
}
