package io.github.dockyardmc.sentinel.common.punishment

import io.github.dockyardmc.sentinel.common.utils.FriendlyLocalDateTime
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs

data class Punishment(
    val type: Type,
    val expires: FriendlyLocalDateTime?,
    val received: FriendlyLocalDateTime,
    val reason: String,
    val punisher: String,
    var active: Boolean = true,
    val acknowledgementString: String? = null,
) {

    companion object {
        val CODEC = Codec.of<Punishment> {
            field("type", Codec.enum(Type::class), Punishment::type)
            field("expires", FriendlyLocalDateTime.CODEC.optional(), Punishment::expires)
            field("received", FriendlyLocalDateTime.CODEC, Punishment::received)
            field("reason", Codecs.String, Punishment::reason)
            field("punisher", Codecs.String, Punishment::punisher)
            field("active", Codecs.Boolean, Punishment::active)
            field("acknowledge_string", Codecs.String.optional(), Punishment::acknowledgementString)
        }
    }

    enum class Type(val kicksPlayer: Boolean) {
        BAN(true),
        KICK(true),
        MUTE(false),
        WARN(false),
    }
}