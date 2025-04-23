package io.github.dockyardmc.sentinel.common

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

enum class PunishmentTimeUnit(val durationUnit: DurationUnit) {
    SECONDS(DurationUnit.SECONDS),
    MINUTES(DurationUnit.MINUTES),
    HOURS(DurationUnit.HOURS),
    DAYS(DurationUnit.DAYS);

    fun getExpireDateFromNow(units: Int): LocalDateTime {
        val date = Clock.System.now().plus(units.toDuration(this.durationUnit))
        return date.toLocalDateTime(TimeZone.UTC)
    }
}