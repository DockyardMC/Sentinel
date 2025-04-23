package io.github.dockyardmc.sentinel.common

import io.github.dockyardmc.sentinel.common.messages.SentinelMessagesConfig
import io.github.dockyardmc.sentinel.common.platform.SentinelPlatform
import io.github.dockyardmc.sentinel.common.punishment.PlayerPunishmentDataCache
import io.github.dockyardmc.sentinel.common.punishment.Punishment
import io.github.dockyardmc.sentinel.common.utils.FriendlyLocalDateTime
import io.github.dockyardmc.sentinel.common.utils.getNow
import io.github.dockyardmc.sentinel.common.utils.getRandomAckString
import io.github.dockyardmc.sentinel.common.utils.toFriendly
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.util.*

object Sentinel {

    lateinit var platform: SentinelPlatform
    const val PREFIX = "<#ff0a30>⛨ <dark_gray>| <gray>"
    const val LIGHT_COLOR = "<#ff757e>"

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
        val data = PlayerPunishmentDataCache.getOrCreate(player)
        val punishments = data.punishments
        var first = punishments.firstOrNull { punishment -> punishment.type == Punishment.Type.BAN && punishment.active }
        if (first?.expires != null) {
            if (first.expires!!.toLocalDateTime().toInstant(TimeZone.UTC) <= Clock.System.now()) {
                unban(player, player.toString())
                first = null
            }
        }
        return first != null
    }

    fun getBanPunishment(player: UUID): Punishment? {
        val punishments = PlayerPunishmentDataCache.getOrCreate(player).punishments
        val firstBan = punishments.firstOrNull { punishment -> punishment.type == Punishment.Type.BAN && punishment.active } ?: return null
        return firstBan
    }

    fun getMutePunishment(player: UUID): Punishment? {
        val punishments = PlayerPunishmentDataCache.getOrCreate(player).punishments
        val firstBan = punishments.firstOrNull { punishment -> punishment.type == Punishment.Type.MUTE && punishment.active } ?: return null
        return firstBan
    }

    fun isMuted(player: UUID): Boolean {
        val data = PlayerPunishmentDataCache.getOrCreate(player)
        val punishments = data.punishments
        var first = punishments.firstOrNull { punishment -> punishment.type == Punishment.Type.MUTE && punishment.active }
        if (first?.expires != null) {
            if (first.expires!!.toLocalDateTime().toInstant(TimeZone.UTC) <= Clock.System.now()) {
                unmute(player, player.toString())
                first = null
            }
        }
        return first != null
    }

    fun hasActiveWarn(player: UUID): Boolean {
        return PlayerPunishmentDataCache.getOrCreate(player).punishments.firstOrNull { punishment -> punishment.type == Punishment.Type.WARN && punishment.active } != null
    }

    fun getFirstActiveWarn(player: UUID): Punishment? {
        val punishments = PlayerPunishmentDataCache.getOrCreate(player).punishments
        val firstWarn = punishments.firstOrNull { punishment -> punishment.type == Punishment.Type.WARN && punishment.active } ?: return null
        return firstWarn
    }

    fun unban(player: UUID, playerName: String) {

        val data = PlayerPunishmentDataCache.getOrCreate(player)
        data.punishments.forEachIndexed { index, punishment ->
            if (punishment.type != Punishment.Type.BAN) return@forEachIndexed
            if (!punishment.active) return@forEachIndexed

            markPunishmentAs(player, index, false)
        }
        platform.broadcastMessageToStaff("<#ff0a30>⛨ <dark_gray>| <gray>Player <#ff757e>$playerName <gray>has been unbanned!")
    }

    fun unmute(player: UUID, playerName: String) {

        val data = PlayerPunishmentDataCache.getOrCreate(player)
        data.punishments.forEachIndexed { index, punishment ->
            if (punishment.type != Punishment.Type.MUTE) return@forEachIndexed
            if (!punishment.active) return@forEachIndexed

            markPunishmentAs(player, index, false)
        }
        platform.broadcastMessageToStaff("<#ff0a30>⛨ <dark_gray>| <gray>Player <#ff757e>$playerName <gray>has been unmuted!")
    }

    fun ban(player: UUID, expires: FriendlyLocalDateTime?, reason: String, punisher: String, playerName: String) {

        if (isBanned(player)) return

        val punishment = Punishment(
            type = Punishment.Type.BAN,
            expires = expires,
            received = LocalDateTime.getNow().toFriendly(),
            reason = reason,
            punisher = punisher
        )
        addPunishment(player, punishment, playerName)
    }

    fun kick(player: UUID, reason: String, punisher: String, playerName: String) {

        val punishment = Punishment(
            type = Punishment.Type.KICK,
            expires = null,
            received = LocalDateTime.getNow().toFriendly(),
            reason = reason,
            punisher = punisher
        )
        addPunishment(player, punishment, playerName)
    }

    fun mute(player: UUID, expires: FriendlyLocalDateTime?, reason: String, punisher: String, playerName: String) {

        if (isMuted(player)) return

        val punishment = Punishment(
            type = Punishment.Type.MUTE,
            expires = expires,
            received = LocalDateTime.getNow().toFriendly(),
            reason = reason,
            punisher = punisher
        )
        addPunishment(player, punishment, playerName)
        platform.sendMessage(player, SentinelMessagesConfig.current.getMutedMessage(punishment))
    }

    fun warn(player: UUID, reason: String, punisher: String, playerName: String) {

        val punishment = Punishment(
            type = Punishment.Type.WARN,
            expires = null,
            received = LocalDateTime.getNow().toFriendly(),
            reason = reason,
            punisher = punisher,
            acknowledgementString = getRandomAckString()

        )
        addPunishment(player, punishment, playerName)
    }

    private fun addPunishment(player: UUID, punishment: Punishment, playerName: String) {

        if (!platform.onPunishment(player, punishment)) return

        val updatedPunishmentData = PlayerPunishmentDataCache.getOrCreate(player)
        updatedPunishmentData.punishments.add(punishment)

        PlayerPunishmentDataCache[player] = updatedPunishmentData
        val message = when (punishment.type) {
            Punishment.Type.BAN -> SentinelMessagesConfig.current.getBannedMessage(punishment)
            Punishment.Type.KICK -> SentinelMessagesConfig.current.getKickedMessage(punishment)
            Punishment.Type.MUTE -> SentinelMessagesConfig.current.getMutedMessage(punishment)
            Punishment.Type.WARN -> TODO()
        }
        if (punishment.type.kicksPlayer) platform.kickPlayer(player, punishment, message)
        platform.broadcastMessageToStaff(SentinelMessagesConfig.current.getStaffPunishmentMessage(punishment, playerName))
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