package io.github.dockyardmc.sentinel.common

import cz.lukynka.hollow.RealmEnum
import cz.lukynka.hollow.initialize
import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.sentinel.common.messages.SentinelMessagesConfig
import io.github.dockyardmc.sentinel.common.platform.SentinelPlatform
import io.github.dockyardmc.sentinel.common.platform.SentinelPlayer
import io.github.dockyardmc.sentinel.common.punishment.PlayerPunishmentData
import io.github.dockyardmc.sentinel.common.punishment.Punishment
import io.github.dockyardmc.sentinel.common.punishment.PunishmentDataStorage
import io.github.dockyardmc.sentinel.common.utils.FriendlyLocalDateTime
import io.github.dockyardmc.sentinel.common.utils.getNow
import io.github.dockyardmc.sentinel.common.utils.getRandomAckString
import io.github.dockyardmc.sentinel.common.utils.toFriendly
import io.realm.kotlin.Realm
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.system.measureTimeMillis

object Sentinel {

    lateinit var platform: SentinelPlatform
    const val PREFIX = "<#ff0a30>⛨ <dark_gray>| <gray>"
    const val LIGHT_COLOR = "<#ff757e>"

    fun initialize(platform: SentinelPlatform) {

        val ms = measureTimeMillis {
            Realm.initialize {
                withSchema<PlayerPunishmentData>()
                withSchema<FriendlyLocalDateTime>()
                withSchema<Punishment>()
                withSchema<RealmEnum>()
            }

            Sentinel.platform = platform
        }

        log("Took $ms to load database", LogType.DEBUG)
    }

    fun setWarnAcknowledged(player: SentinelPlayer, punishment: Punishment) {
        punishment.active = false
        val data = PunishmentDataStorage.getOrCreate(player.uuid)

        val index = data.punishments.indexOf(punishment)
        data.punishments[index] = punishment
        PunishmentDataStorage.write(data)
        platform.sendMessage(player, "$PREFIX<lime>You have acknowledged your warning, you can send messages in chat again!")
    }

    fun sendWarningAcknowledgeMessage(player: SentinelPlayer, punishment: Punishment) {
        platform.sendMessage(player, SentinelMessagesConfig.current.getWarnedMessage(punishment))
    }

    fun getFirstPunishmentOfType(type: Punishment.Type, player: SentinelPlayer): Punishment? {
        val punishments = PunishmentDataStorage.getOrCreate(player.uuid).punishments
        val firstBan = punishments.firstOrNull { punishment -> punishment.type == type && punishment.active } ?: return null
        return firstBan
    }

    fun hasActivePunishmentOfType(type: Punishment.Type, player: SentinelPlayer): Boolean {
        val data = PunishmentDataStorage.getOrCreate(player.uuid)
        removeExpiredPunishments(player)
        return data.punishments.any { punishment -> punishment.active && punishment.type == type }
    }

    fun removeExpiredPunishments(player: SentinelPlayer) {
        val data = PunishmentDataStorage.getOrCreate(player.uuid)
        data.punishments.filter { punishment -> punishment.active && punishment.expires != null }.forEach { punishment ->
            if (punishment.expires!!.toLocalDateTime().toInstant(TimeZone.UTC) > Clock.System.now()) return@forEach
            punishment.active = false
            PunishmentDataStorage.write(data)
            platform.broadcastMessageToStaff("<#ff0a30>⛨ <dark_gray>| <gray>Punishment of type <white>${punishment.type.name}<gray> for player <#ff757e>${player.username}<gray> has expired!")
        }
    }

    fun unban(player: SentinelPlayer) {

        val data = PunishmentDataStorage.getOrCreate(player.uuid)
        data.punishments.forEachIndexed { index, punishment ->
            if (punishment.type != Punishment.Type.BAN) return@forEachIndexed
            if (!punishment.active) return@forEachIndexed

            markPunishmentAs(player, index, false)
        }
        platform.broadcastMessageToStaff("<#ff0a30>⛨ <dark_gray>| <gray>Player <#ff757e>${player.username} <gray>has been unbanned!")
    }

    fun unmute(player: SentinelPlayer) {

        val data = PunishmentDataStorage.getOrCreate(player.uuid)
        data.punishments.forEachIndexed { index, punishment ->
            log("$punishment")
            if (punishment.type != Punishment.Type.MUTE) return@forEachIndexed
            if (!punishment.active) return@forEachIndexed

            markPunishmentAs(player, index, false)
        }
        platform.broadcastMessageToStaff("<#ff0a30>⛨ <dark_gray>| <gray>Player <#ff757e>${player.username} <gray>has been unmuted!")
    }

    fun ban(player: SentinelPlayer, expires: FriendlyLocalDateTime?, reason: String, punisher: String) {

        if (hasActivePunishmentOfType(Punishment.Type.BAN, player)) return

        val punishment = Punishment(
            realmType = RealmEnum.of(Punishment.Type.BAN),
            expires = expires,
            received = LocalDateTime.getNow().toFriendly(),
            reason = reason,
            punisher = punisher
        )
        addPunishment(player, punishment)
    }

    fun kick(player: SentinelPlayer, reason: String, punisher: String) {

        val punishment = Punishment(
            realmType = RealmEnum.of(Punishment.Type.KICK),
            expires = null,
            received = LocalDateTime.getNow().toFriendly(),
            reason = reason,
            punisher = punisher
        )
        addPunishment(player, punishment)
    }

    fun mute(player: SentinelPlayer, expires: FriendlyLocalDateTime?, reason: String, punisher: String) {

        if (hasActivePunishmentOfType(Punishment.Type.MUTE, player)) return

        val punishment = Punishment(
            realmType = RealmEnum.of(Punishment.Type.MUTE),
            expires = expires,
            received = LocalDateTime.getNow().toFriendly(),
            reason = reason,
            punisher = punisher
        )
        addPunishment(player, punishment)
        platform.sendMessage(player, SentinelMessagesConfig.current.getMutedMessage(punishment))
    }

    fun warn(player: SentinelPlayer, reason: String, punisher: String) {

        val punishment = Punishment(
            realmType = RealmEnum.of(Punishment.Type.WARN),
            expires = null,
            received = LocalDateTime.getNow().toFriendly(),
            reason = reason,
            punisher = punisher,
            acknowledgementString = getRandomAckString()

        )
        addPunishment(player, punishment)
        sendWarningAcknowledgeMessage(player, punishment)
    }

    private fun addPunishment(player: SentinelPlayer, punishment: Punishment) {

        if (!platform.onPunishment(player, punishment)) return

        val updatedPunishmentData = PunishmentDataStorage.getOrCreate(player.uuid)
        updatedPunishmentData.punishments.add(punishment)

        PunishmentDataStorage.write(updatedPunishmentData)
        val message = when (punishment.type) {
            Punishment.Type.BAN -> SentinelMessagesConfig.current.getBannedMessage(punishment)
            Punishment.Type.KICK -> SentinelMessagesConfig.current.getKickedMessage(punishment)
            Punishment.Type.MUTE -> SentinelMessagesConfig.current.getMutedMessage(punishment)
            Punishment.Type.WARN -> SentinelMessagesConfig.current.getWarnedMessage(punishment)
        }

        if (punishment.type.kicksPlayer) platform.kickPlayer(player, punishment, message)
        platform.broadcastMessageToStaff(SentinelMessagesConfig.current.getStaffPunishmentMessage(punishment, player.username))
    }

    private fun markPunishmentAs(player: SentinelPlayer, index: Int, active: Boolean) {
        val punishmentData = PunishmentDataStorage.getOrCreate(player.uuid)

        val punishment = punishmentData.punishments.getOrNull(index) ?: throw IllegalArgumentException("Punishment with id/index $index does not exist for uuid $player")
        punishment.active = active
        platform.onPunishmentStateChange(player, punishment, active)

        punishmentData.punishments[index] = punishment

        PunishmentDataStorage.write(punishmentData)
    }
}