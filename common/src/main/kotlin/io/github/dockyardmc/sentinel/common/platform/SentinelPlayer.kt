package io.github.dockyardmc.sentinel.common.platform

import io.github.dockyardmc.sentinel.common.Sentinel
import io.github.dockyardmc.sentinel.common.punishment.PlayerPunishmentData
import io.github.dockyardmc.sentinel.common.punishment.PlayerPunishmentDataCache
import io.github.dockyardmc.sentinel.common.punishment.Punishment
import java.util.*

data class SentinelPlayer(val uuid: UUID, val username: String) {
    fun getPunishmentData(): PlayerPunishmentData {
        return PlayerPunishmentDataCache.getOrCreate(this)
    }

    fun isBanned(): Boolean {
        return Sentinel.hasActivePunishmentOfType(Punishment.Type.BAN, this)
    }

    fun isMuted(): Boolean {
        return Sentinel.hasActivePunishmentOfType(Punishment.Type.MUTE, this)
    }

    fun hasActiveWarning(): Boolean {
        return Sentinel.hasActivePunishmentOfType(Punishment.Type.WARN, this)
    }

    fun getPunishmentOfType(type: Punishment.Type): Punishment? {
        return Sentinel.getFirstPunishmentOfType(type, this)
    }

}