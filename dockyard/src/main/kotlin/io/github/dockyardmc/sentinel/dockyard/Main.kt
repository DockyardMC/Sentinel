package io.github.dockyardmc.sentinel.dockyard

import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.motd.ServerStatusManager
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.sentinel.common.Sentinel
import io.github.dockyardmc.sentinel.common.messages.SentinelMessagesConfig
import io.github.dockyardmc.sentinel.common.platform.SentinelPlatform
import io.github.dockyardmc.sentinel.dockyard.modules.BanModule
import io.github.dockyardmc.sentinel.dockyard.modules.MuteModule
import io.github.dockyardmc.sentinel.dockyard.modules.SentinelModule

fun main() {
    val server = DockyardServer {
        withIp("0.0.0.0")
        withPort(25565)
    }

    Sentinel.initialize(SentinelPlatformDockyard())
    SentinelModule.register(BanModule(), MuteModule())
    SentinelMessagesConfig.initialize()

    Events.on<PlayerJoinEvent> { event ->
        event.player.permissions.add("*")
        event.player.gameMode.value = GameMode.CREATIVE
    }

    ServerStatusManager.defaultDescription.value = "<#ff0000>Sentinel <dark_gray>| <gray>Test Server"

    server.start()
}