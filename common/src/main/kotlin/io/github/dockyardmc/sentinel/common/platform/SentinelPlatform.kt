package io.github.dockyardmc.sentinel.common.platform

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyard.cz.lukynka.hollow.Hollow
import io.github.dockyardmc.sentinel.common.punishment.PlayerPunishmentDataCache
import io.github.dockyardmc.sentinel.common.punishment.Punishment
import io.github.dockyardmc.sentinel.common.Sentinel
import java.util.UUID
import kotlin.system.measureTimeMillis

interface SentinelPlatform {

    companion object {
        fun initialize(platform: SentinelPlatform) {
            val ms = measureTimeMillis {
                Sentinel.platform = platform
                Hollow.initialize("sentinel")
                PlayerPunishmentDataCache.initialize()
            }
            log("Took $ms to load database", LogType.PERFORMANCE)
        }
    }

    fun getPlatformName(): String
    fun getPlatformVersion(): String
    fun getPlatformType(): PlatformType

    fun onPunishment(uuid: UUID, punishment: Punishment): Boolean
    fun onPunishmentStateChange(uuid: UUID, punishment: Punishment, newState: Boolean)
    fun kickPlayer(uuid: UUID, punishment: Punishment, kickMessage: String)
    fun sendMessage(uuid: UUID, message: String)
    fun broadcastMessageToStaff(message: String)

    enum class PlatformType {
        SERVER,
        PROXY,
        STANDALONE,
    }
}