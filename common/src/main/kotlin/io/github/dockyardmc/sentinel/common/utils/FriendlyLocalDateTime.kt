package io.github.dockyardmc.sentinel.common.utils

import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs
import kotlinx.datetime.*

data class FriendlyLocalDateTime(val epoch: Long) {

    fun toLocalDateTime(): LocalDateTime {
        return Instant.fromEpochMilliseconds(epoch).toLocalDateTime(TimeZone.UTC)
    }

    companion object {

        val CODEC = Codec.of<FriendlyLocalDateTime> {
            field("epoch", Codecs.Long, FriendlyLocalDateTime::epoch)
        }

        fun fromLocalDateTime(localDateTime: LocalDateTime): FriendlyLocalDateTime {
            return FriendlyLocalDateTime(localDateTime.toInstant(TimeZone.UTC).toEpochMilliseconds())
        }
    }
}

fun LocalDateTime.toFriendly(): FriendlyLocalDateTime {
    return FriendlyLocalDateTime.fromLocalDateTime(this)
}