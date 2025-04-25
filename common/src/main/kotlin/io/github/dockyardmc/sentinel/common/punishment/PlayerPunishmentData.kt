package io.github.dockyardmc.sentinel.common.punishment

import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs
import java.util.*

data class PlayerPunishmentData(
    val uuid: UUID,
    var lastKnownUsername: String,
    val punishments: MutableList<Punishment>,
) {
    companion object {
        val CODEC = Codec.of(
            "uuid", Codecs.UUID, PlayerPunishmentData::uuid,
            "last_known_username", Codecs.String, PlayerPunishmentData::lastKnownUsername,
            "punishments", Punishment.CODEC.mutableList(), PlayerPunishmentData::punishments,
            ::PlayerPunishmentData
        )
    }
}