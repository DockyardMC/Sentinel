package io.github.dockyardmc.sentinel.dockyard.modules

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager

interface SentinelModule {
    fun register()

    companion object {

        fun suggestPlayers(player: Player): List<String> {
            return PlayerManager.players.map { p -> p.username }
        }

        fun register(vararg commands: SentinelModule) {
            commands.forEach { command -> command.register() }
        }
    }
}