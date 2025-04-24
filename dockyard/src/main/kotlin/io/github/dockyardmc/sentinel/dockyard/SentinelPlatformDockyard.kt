package io.github.dockyardmc.sentinel.dockyard

import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.extentions.sendMessage
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.sentinel.common.platform.SentinelPlatform
import io.github.dockyardmc.sentinel.common.platform.SentinelPlayer
import io.github.dockyardmc.sentinel.common.punishment.Punishment

class SentinelPlatformDockyard : SentinelPlatform {

    override fun getPlatformName(): String {
        return "DockyardMC"
    }

    override fun getPlatformVersion(): String {
        return DockyardServer.versionInfo.dockyardVersion
    }

    override fun getPlatformType(): SentinelPlatform.PlatformType {
        return SentinelPlatform.PlatformType.SERVER
    }

    override fun onPunishment(player: SentinelPlayer, punishment: Punishment): Boolean {
        return true
    }

    override fun onPunishmentStateChange(player: SentinelPlayer, punishment: Punishment, newState: Boolean) {
        TODO("events")
    }

    override fun kickPlayer(player: SentinelPlayer, punishment: Punishment, kickMessage: String) {
        val dockyardPlayer = PlayerManager.getPlayerByUUIDOrNull(player.uuid) ?: return
        log("$dockyardPlayer")
        dockyardPlayer.kick(kickMessage)
    }

    override fun sendMessage(player: SentinelPlayer, message: String) {
        val dockyardPlayer = PlayerManager.getPlayerByUUIDOrNull(player.uuid) ?: return
        dockyardPlayer.sendMessage(message)
    }

    override fun broadcastMessageToStaff(message: String) {
        PlayerManager.players.filter { player -> player.hasPermission("sentinel.staff.broadcast") }.sendMessage(message)
    }
}