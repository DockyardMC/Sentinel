package io.github.dockyardmc.sentinel.dockyard.modules

import io.github.dockyardmc.commands.BrigadierStringType
import io.github.dockyardmc.commands.CommandException
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.sentinel.common.Sentinel
import io.github.dockyardmc.sentinel.dockyard.modules.SentinelModule.Companion.suggestPlayers
import io.github.dockyardmc.utils.MojangUtil
import java.util.concurrent.CompletableFuture

class BanModule : SentinelModule {

    override fun register() {

        Events.on<PlayerConnectEvent> { event ->
            val uuid = event.player.uuid
            if(Sentinel.isBanned(uuid)) {
                val punishment = Sentinel.getBanPunishment(uuid)!!
                event.player.kick("You are banned: ${punishment.reason}")
            }
        }

        Commands.add("/ban") {
            addArgument("player", StringArgument(), ::suggestPlayers)
            addArgument("reason", StringArgument(BrigadierStringType.GREEDY_PHRASE))

            withPermission("sentinel.command.ban")
            withDescription("Bans a player")

            execute { ctx ->
                val playerUsername = getArgument<String>("player")
                val player = PlayerManager.getPlayerByUsernameOrNull(playerUsername)
                val reason = getArgument<String>("reason")
                val punisher = if (ctx.isPlayer) ctx.player!!.username else "System"

                if (player != null) {
                    Sentinel.ban(player.uuid, null, reason, punisher)
                    ctx.sendMessage("<lime>Banned $player!")
                } else {
                    CompletableFuture.supplyAsync {
                        ctx.sendMessage("<gray>Fetching uuid from username..")
                        MojangUtil.getUUIDFromUsername(playerUsername)
                    }.thenAccept { uuid ->
                        if (uuid == null) throw CommandException("Player with name $playerUsername does not exist!")

                        Sentinel.ban(uuid, null, reason, punisher)
                        ctx.sendMessage("<lime>Banned $playerUsername!")
                    }
                }
            }
        }
    }
}