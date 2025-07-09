package io.github.dockyardmc.sentinel.common.utils

import io.realm.kotlin.types.RealmObject
import kotlinx.datetime.*

class FriendlyLocalDateTime(initial: Long) : RealmObject {
    var time: Long = initial

    constructor() : this(0L)

    fun toLocalDateTime(): LocalDateTime {
        return Instant.fromEpochMilliseconds(time).toLocalDateTime(TimeZone.UTC)
    }

    companion object {
        fun fromLocalDateTime(localDateTime: LocalDateTime): FriendlyLocalDateTime {
            return FriendlyLocalDateTime(localDateTime.toInstant(TimeZone.UTC).toEpochMilliseconds())
        }
    }
}

fun LocalDateTime.toFriendly(): FriendlyLocalDateTime {
    return FriendlyLocalDateTime.fromLocalDateTime(this)
}