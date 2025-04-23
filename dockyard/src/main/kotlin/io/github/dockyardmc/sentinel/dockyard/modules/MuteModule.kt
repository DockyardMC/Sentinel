package io.github.dockyardmc.sentinel.dockyard.modules

import cz.lukynka.prettylog.log
import io.github.dockyardmc.commands.*
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PacketReceivedEvent
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPlayerChatMessagePacket
import io.github.dockyardmc.sentinel.common.PunishmentTimeUnit
import io.github.dockyardmc.sentinel.common.Sentinel
import io.github.dockyardmc.sentinel.common.messages.SentinelMessagesConfig
import io.github.dockyardmc.sentinel.common.utils.toFriendly
import io.github.dockyardmc.sentinel.dockyard.getOrFetchPlayerUUID
import io.github.dockyardmc.sentinel.dockyard.modules.SentinelModule.Companion.suggestPlayers

class MuteModule : SentinelModule {

    override fun register() {

        Events.on<PacketReceivedEvent> { event ->
            if(event.packet !is ServerboundPlayerChatMessagePacket) return@on
            val packet = event.packet as ServerboundPlayerChatMessagePacket
            val player = event.processor.player
            val uuid = player.uuid

            if (Sentinel.isMuted(uuid)) {
                event.cancel()
                val punishment = Sentinel.getMutePunishment(uuid)!!
                Sentinel.platform.broadcastMessageToStaff(SentinelMessagesConfig.current.getMutedPlayerTriedToSpeakMessage(punishment, player.username, packet.message))
                player.sendMessage(SentinelMessagesConfig.current.getMutedMessage(punishment))
            }
        }

        Commands.add("/unmute") {
            addArgument("player", StringArgument(), ::suggestPlayers)
            withPermission("sentinel.command.mute")
            withDescription("Unmutes a player")
            execute { ctx ->
                val playerUsername = getArgument<String>("player")

                getOrFetchPlayerUUID(playerUsername).thenAccept { uuid ->

                    if (uuid == null) throw CommandException("${Sentinel.PREFIX}<red>Player with the username $playerUsername does not exist!")
                    if (!Sentinel.isMuted(uuid)) throw CommandException("${Sentinel.PREFIX}<red>Player $playerUsername is not muted!")

                    Sentinel.unmute(uuid, playerUsername)
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

        Commands.add("/mute") {
            addArgument("player", StringArgument(), ::suggestPlayers)
            addArgument("time", IntArgument())
            addArgument("time_unit", EnumArgument(PunishmentTimeUnit::class))
            addArgument("reason", StringArgument(BrigadierStringType.GREEDY_PHRASE))

            withPermission("sentinel.command.mute")
            withDescription("Mutes a player")

            execute { ctx ->
                val playerUsername = getArgument<String>("player")
                val reason = getArgument<String>("reason")
                val punisher = if (ctx.isPlayer) ctx.player!!.username else "System"

                val time = getArgument<Int>("time")
                val timeUnit = getEnumArgument<PunishmentTimeUnit>("time_unit")

                val expires = if(time == -1) null else timeUnit.getExpireDateFromNow(time)

                getOrFetchPlayerUUID(playerUsername).thenAccept { uuid ->
                    if (uuid == null) throw CommandException("${Sentinel.PREFIX}<red>Player with the username $playerUsername does not exist!")
                    if (Sentinel.isMuted(uuid)) throw CommandException("${Sentinel.PREFIX}<red>Player $playerUsername is already muted!")

                    Sentinel.mute(uuid, expires?.toFriendly(), reason, punisher, playerUsername)
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