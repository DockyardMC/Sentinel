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
        val CODEC = Codec.of(
            "type", Codec.enum<Type>(), Punishment::type,
            "expires", FriendlyLocalDateTime.CODEC.optional(), Punishment::expires,
            "received", FriendlyLocalDateTime.CODEC, Punishment::received,
            "reason", Codecs.String, Punishment::reason,
            "punisher", Codecs.String, Punishment::punisher,
            "active", Codecs.Boolean, Punishment::active,
            "acknowledge_string", Codecs.String.optional(), Punishment::acknowledgementString,
            ::Punishment
        )
    }

    enum class Type(val kicksPlayer: Boolean, val past: String) {
        BAN(true, "BANNED"),
        KICK(true, "KICKED"),
        MUTE(false, "MUTED"),
        WARN(false, "WARNED"),
    }
}