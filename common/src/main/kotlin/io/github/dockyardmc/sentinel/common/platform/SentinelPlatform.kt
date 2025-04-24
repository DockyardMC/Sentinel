package io.github.dockyardmc.sentinel.common.platform

import io.github.dockyardmc.sentinel.common.punishment.Punishment

interface SentinelPlatform {

    fun getPlatformName(): String
    fun getPlatformVersion(): String
    fun getPlatformType(): PlatformType

    fun onPunishment(player: SentinelPlayer, punishment: Punishment): Boolean
    fun onPunishmentStateChange(player: SentinelPlayer, punishment: Punishment, newState: Boolean)
    fun kickPlayer(player: SentinelPlayer, punishment: Punishment, kickMessage: String)
    fun sendMessage(player: SentinelPlayer, message: String)
    fun broadcastMessageToStaff(message: String)

    enum class PlatformType {
        SERVER,
        PROXY,
        STANDALONE,
    }
}