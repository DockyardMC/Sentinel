package io.github.dockyardmc.sentinel.dockyard

import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.sentinel.common.platform.SentinelPlatform
import io.github.dockyardmc.sentinel.common.punishment.Punishment
import java.util.*

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

    override fun onPunishment(uuid: UUID, punishment: Punishment): Boolean {
        return true //TODO(event)
    }

    override fun onPunishmentStateChange(uuid: UUID, punishment: Punishment, newState: Boolean) {
    }

    override fun kickPlayer(uuid: UUID, punishment: Punishment, kickMessage: String) {
        val player = PlayerManager.getPlayerByUUID(uuid)
        log("$player")
        player.kick(kickMessage)
    }

    override fun sendMessage(uuid: UUID, message: String) {
        val player = PlayerManager.getPlayerByUUID(uuid)
        player.sendMessage(message)
    }
}