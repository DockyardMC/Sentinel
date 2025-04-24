package io.github.dockyardmc.sentinel.dockyard.modules

import cz.lukynka.prettylog.log
import io.github.dockyardmc.commands.*
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.sentinel.common.PunishmentTimeUnit
import io.github.dockyardmc.sentinel.common.Sentinel
import io.github.dockyardmc.sentinel.common.messages.SentinelMessagesConfig
import io.github.dockyardmc.sentinel.common.punishment.Punishment
import io.github.dockyardmc.sentinel.common.utils.toFriendly
import io.github.dockyardmc.sentinel.dockyard.getOrFetchSentinelPlayer
import io.github.dockyardmc.sentinel.dockyard.modules.SentinelModule.Companion.suggestPlayers
import io.github.dockyardmc.sentinel.dockyard.toSentinelPlayer

class BanModule : SentinelModule {

    override fun register() {

        Events.on<PlayerConnectEvent> { event ->
            val sentinelPlayer = event.player.toSentinelPlayer()
            if (sentinelPlayer.isBanned()) {
                val punishment = sentinelPlayer.getPunishmentOfType(Punishment.Type.BAN)!!
                event.player.kick(SentinelMessagesConfig.current.getBannedMessage(punishment))
                Sentinel.platform.broadcastMessageToStaff(SentinelMessagesConfig.current.getBannedPlayerTriedToSpeakMessage(punishment, event.player.username))
            }
        }

        Commands.add("/unban") {
            addArgument("player", StringArgument(), ::suggestPlayers)
            withPermission("sentinel.command.ban")
            withDescription("Unbans a banned player")
            execute { ctx ->
                val playerUsername = getArgument<String>("player")

                getOrFetchSentinelPlayer(playerUsername).thenAccept { player ->
                    if (player == null) throw CommandException("${Sentinel.PREFIX}<red>Player with the username $playerUsername does not exist!")
                    if (!player.isBanned()) throw CommandException("${Sentinel.PREFIX}<red>Player $playerUsername is not banned!")

                    Sentinel.unban(player)

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

        Commands.add("/ban") {
            addArgument("player", StringArgument(), ::suggestPlayers)
            addArgument("time", IntArgument())
            addArgument("time_unit", EnumArgument(PunishmentTimeUnit::class))
            addArgument("reason", StringArgument(BrigadierStringType.GREEDY_PHRASE))

            withPermission("sentinel.command.ban")
            withDescription("Bans a player")

            execute { ctx ->
                val playerUsername = getArgument<String>("player")
                val reason = getArgument<String>("reason")
                val punisher = if (ctx.isPlayer) ctx.player!!.username else "System"
                val time = getArgument<Int>("time")
                val timeUnit = getEnumArgument<PunishmentTimeUnit>("time_unit")

                val expires = if (time == -1) null else timeUnit.getExpireDateFromNow(time)

                getOrFetchSentinelPlayer(playerUsername).thenAccept { sentinelPlayer ->
                    if (sentinelPlayer == null) throw CommandException("${Sentinel.PREFIX}<red>Player with the username $playerUsername does not exist!")
                    if (sentinelPlayer.isBanned()) throw CommandException("${Sentinel.PREFIX}<red>Player $playerUsername is already banned!")

                    Sentinel.ban(sentinelPlayer, expires?.toFriendly(), reason, punisher)
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