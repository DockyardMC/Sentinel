package io.github.dockyardmc.sentinel.dockyard.modules

import io.github.dockyardmc.commands.BrigadierStringType
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.sentinel.common.Sentinel
import io.github.dockyardmc.sentinel.dockyard.toSentinelPlayer

class KickModule : SentinelModule {

    override fun register() {

        Commands.add("/kick") {

            addArgument("player", PlayerArgument())
            addArgument("reason", StringArgument(BrigadierStringType.GREEDY_PHRASE))

            withPermission("sentinel.command.kick")
            withDescription("Kicks a player")

            execute { ctx ->
                val player = getArgument<Player>("player")
                val reason = getArgument<String>("reason")

                Sentinel.kick(player.toSentinelPlayer(), reason, if (ctx.isPlayer) ctx.getPlayerOrThrow().username else "System")
            }
        }
    }
}