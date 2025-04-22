package io.github.dockyardmc.sentinel.common.utils

import kotlinx.datetime.*

fun LocalDateTime.Companion.getNow(): LocalDateTime {
    val now = Clock.System.now()
    return now.toLocalDateTime(TimeZone.UTC)
}