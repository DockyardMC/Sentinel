package io.github.dockyardmc.sentinel.common

import io.github.dockyardmc.sentinel.common.platform.SentinelPlatform
import io.github.dockyardmc.sentinel.common.punishment.PlayerPunishmentDataCache
import io.github.dockyardmc.sentinel.common.punishment.Punishment
import io.github.dockyardmc.sentinel.common.utils.FriendlyLocalDateTime
import io.github.dockyardmc.sentinel.common.utils.getNow
import io.github.dockyardmc.sentinel.common.utils.getRandomAckString
import io.github.dockyardmc.sentinel.common.utils.toFriendly
import kotlinx.datetime.LocalDateTime
import java.util.*

object Sentinel {

    lateinit var platform: SentinelPlatform

    fun setWarnAcknowledged(player: UUID, punishment: Punishment) {
        punishment.active = false
        val data = PlayerPunishmentDataCache.getOrCreate(player)
        val index = data.punishments.indexOf(punishment)
        data.punishments[index] = punishment
        PlayerPunishmentDataCache[player] = data
    }

    fun sendWarningAcknowledgeMessage(player: UUID, punishment: Punishment) {
        platform.sendMessage(player, " ")
        platform.sendMessage(player, "You have been warned for: ${punishment.reason}")
        platform.sendMessage(player, "Please type following characters in chat: <yellow>${punishment.acknowledgementString!!}")
        platform.sendMessage(player, " ")
    }

    fun isBanned(player: UUID): Boolean {
        return PlayerPunishmentDataCache.getOrCreate(player).punishments.firstOrNull { punishment -> punishment.type == Punishment.Type.BAN && punishment.active } != null
    }

    fun getBanPunishment(player: UUID): Punishment? {
        val punishments = PlayerPunishmentDataCache.getOrCreate(player).punishments
        val firstBan = punishments.firstOrNull { punishment -> punishment.type == Punishment.Type.BAN && punishment.active } ?: return null
        return firstBan
    }

    fun isMuted(player: UUID): Boolean {
        return PlayerPunishmentDataCache.getOrCreate(player).punishments.firstOrNull { punishment -> punishment.type == Punishment.Type.MUTE && punishment.active } != null
    }

    fun hasActiveWarn(player: UUID): Boolean {
        return PlayerPunishmentDataCache.getOrCreate(player).punishments.firstOrNull { punishment -> punishment.type == Punishment.Type.WARN && punishment.active } != null
    }

    fun getFirstActiveWarn(player: UUID): Punishment? {
        val punishments = PlayerPunishmentDataCache.getOrCreate(player).punishments
        val firstWarn = punishments.firstOrNull { punishment -> punishment.type == Punishment.Type.WARN && punishment.active } ?: return null
        return firstWarn
    }

    fun unban(player: UUID) {

        if (!isBanned(player)) return

        val data = PlayerPunishmentDataCache.getOrCreate(player)
        data.punishments.forEachIndexed { index, punishment ->
            if (punishment.type != Punishment.Type.BAN) return@forEachIndexed
            if (!punishment.active) return@forEachIndexed

            markPunishmentAs(player, index, false)
        }
    }

    fun unmute(player: UUID) {

        if (!isMuted(player)) return

        val data = PlayerPunishmentDataCache.getOrCreate(player)
        data.punishments.forEachIndexed { index, punishment ->
            if (punishment.type != Punishment.Type.MUTE) return@forEachIndexed
            if (!punishment.active) return@forEachIndexed

            markPunishmentAs(player, index, false)
        }
    }

    fun ban(player: UUID, expires: FriendlyLocalDateTime?, reason: String, punisher: String) {

        if (isBanned(player)) return

        val punishment = Punishment(
            type = Punishment.Type.BAN,
            expires = expires,
            received = LocalDateTime.getNow().toFriendly(),
            reason = reason,
            punisher = punisher
        )
        addPunishment(player, punishment)
    }

    fun kick(player: UUID, reason: String, punisher: String) {

        val punishment = Punishment(
            type = Punishment.Type.KICK,
            expires = null,
            received = LocalDateTime.getNow().toFriendly(),
            reason = reason,
            punisher = punisher
        )
        addPunishment(player, punishment)
    }

    fun mute(player: UUID, expires: FriendlyLocalDateTime?, reason: String, punisher: String) {

        if (isMuted(player)) return

        val punishment = Punishment(
            type = Punishment.Type.MUTE,
            expires = expires,
            received = LocalDateTime.getNow().toFriendly(),
            reason = reason,
            punisher = punisher
        )
        addPunishment(player, punishment)
    }

    fun warn(player: UUID, reason: String, punisher: String) {

        val punishment = Punishment(
            type = Punishment.Type.WARN,
            expires = null,
            received = LocalDateTime.getNow().toFriendly(),
            reason = reason,
            punisher = punisher,
            acknowledgementString = getRandomAckString()

        )
        addPunishment(player, punishment)
    }

    private fun addPunishment(player: UUID, punishment: Punishment) {

        if (!platform.onPunishment(player, punishment)) return

        val updatedPunishmentData = PlayerPunishmentDataCache.getOrCreate(player)
        updatedPunishmentData.punishments.add(punishment)

        PlayerPunishmentDataCache[player] = updatedPunishmentData
        if (punishment.type.kicksPlayer) platform.kickPlayer(player, punishment, "rip bozo") //TODO(Localization system)
    }

    private fun markPunishmentAs(player: UUID, index: Int, active: Boolean) {
        val punishmentData = PlayerPunishmentDataCache.getOrCreate(player)

        val punishment = punishmentData.punishments.getOrNull(index) ?: throw IllegalArgumentException("Punishment with id/index $index does not exist for uuid $player")
        punishment.active = active
        platform.onPunishmentStateChange(player, punishment, active)

        punishmentData.punishments[index] = punishment

        PlayerPunishmentDataCache[player] = punishmentData
    }
}