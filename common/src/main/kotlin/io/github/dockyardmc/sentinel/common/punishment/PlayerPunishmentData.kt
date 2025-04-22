package io.github.dockyardmc.sentinel.common.punishment

import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs
import java.util.*

data class PlayerPunishmentData(
    val uuid: UUID,
    val punishments: MutableList<Punishment>,
) {
    companion object {
        val CODEC = Codec.of<PlayerPunishmentData> {
            field("uuid", Codecs.UUID, PlayerPunishmentData::uuid)
            field("punishments", Punishment.CODEC.list(), PlayerPunishmentData::punishments)
        }
    }
}