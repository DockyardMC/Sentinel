package io.github.dockyardmc.sentinel.common.platform

import io.github.dockyardmc.sentinel.common.punishment.Punishment
import java.util.*

class StandaloneSentinelPlatform : SentinelPlatform {

    override fun getPlatformName(): String {
        return "Standalone"
    }

    override fun getPlatformVersion(): String {
        return "1.0"
    }

    override fun getPlatformType(): SentinelPlatform.PlatformType {
        return SentinelPlatform.PlatformType.STANDALONE
    }

    override fun onPunishment(uuid: UUID, punishment: Punishment): Boolean {
        return true
    }

    override fun onPunishmentStateChange(uuid: UUID, punishment: Punishment, newState: Boolean) {
    }

    override fun kickPlayer(uuid: UUID, punishment: Punishment, kickMessage: String) {

    }

    override fun sendMessage(uuid: UUID, message: String) {
        println(message)
    }

}