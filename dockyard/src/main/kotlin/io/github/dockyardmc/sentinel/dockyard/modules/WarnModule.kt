package io.github.dockyardmc.sentinel.dockyard.modules

import cz.lukynka.prettylog.log
import io.github.dockyardmc.commands.BrigadierStringType
import io.github.dockyardmc.commands.CommandException
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketReceivedEvent
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPlayerChatMessagePacket
import io.github.dockyardmc.sentinel.common.Sentinel
import io.github.dockyardmc.sentinel.common.punishment.Punishment
import io.github.dockyardmc.sentinel.dockyard.getOrFetchSentinelPlayer
import io.github.dockyardmc.sentinel.dockyard.toSentinelPlayer

class WarnModule : SentinelModule {

    override fun register() {

        Events.on<PacketReceivedEvent> { event ->
            if (event.packet !is ServerboundPlayerChatMessagePacket) return@on
            val player = event.processor.player
            val packet = event.packet as ServerboundPlayerChatMessagePacket

            if (!Sentinel.hasActivePunishmentOfType(Punishment.Type.WARN, player.toSentinelPlayer())) return@on
            val sentinelPlayer = player.toSentinelPlayer()
            val punishment = sentinelPlayer.getPunishmentOfType(Punishment.Type.WARN)!!

            event.cancel()

            if (packet.message == punishment.acknowledgementString) {
                Sentinel.setWarnAcknowledged(sentinelPlayer, punishment)
            } else {
                Sentinel.sendWarningAcknowledgeMessage(sentinelPlayer, punishment)
            }
        }

        Commands.add("/warn") {
            addArgument("player", StringArgument(), SentinelModule.Companion::suggestPlayers)
            addArgument("reason", StringArgument(BrigadierStringType.GREEDY_PHRASE))

            withPermission("sentinel.command.warn")
            withDescription("Warns a player")
            execute { ctx ->
                val playerUsername = getArgument<String>("player")
                val punisher = if (ctx.isPlayer) ctx.player!!.username else "System"
                val reason = getArgument<String>("reason")

                getOrFetchSentinelPlayer(playerUsername).thenAccept { sentinelPlayer ->
                    if (sentinelPlayer == null) throw CommandException("${Sentinel.PREFIX}<red>Player with the username $playerUsername does not exist!")
                    Sentinel.warn(sentinelPlayer, reason, punisher)
                }.exceptionally { exception ->
                    val cause = exception.cause ?: exception
                    if (cause is CommandException) {
                        ctx.sendMessage(cause.message)
                        null
                    } else {
                        log(cause as Exception)
                        ctx.sendMessage("There was an error while fetching uuid of player: $cause")
                        null
                    }
                }
            }
        }
    }
}