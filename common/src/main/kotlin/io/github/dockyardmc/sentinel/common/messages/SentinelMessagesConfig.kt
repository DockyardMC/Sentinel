package io.github.dockyardmc.sentinel.common.messages

import io.github.dockyardmc.sentinel.common.punishment.Punishment
import io.github.dockyardmc.sentinel.common.utils.formatted
import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs
import io.github.dockyardmc.tide.transcoders.Toml
import io.github.dockyardmc.tide.transcoders.TomlTranscoder
import java.io.File

object SentinelMessagesConfig {

    const val COLOR = "<#ff0a30>"
    const val COLOR_LIGHT = "<#ff757e>"
    const val PREFIX = "$COLOR⛨ <b>SENTINEL</b> ⛨"

    private val CONFIG_FILE = File("./sentinel.toml")
    lateinit var current: Messages

    fun initialize() {
        if (!CONFIG_FILE.exists()) {
            val toml = Toml()
            Messages.CODEC.writeTranscoded(TomlTranscoder, toml, Messages(), "")

            CONFIG_FILE.createNewFile()
            CONFIG_FILE.writeText(toml.getAsString())
        }

        val toml = Toml.fromString(CONFIG_FILE.readText())
        current = Messages.CODEC.readTranscoded(TomlTranscoder, toml, "")
    }

    data class Messages(
        val banned: String = "{COLOR}⛨ <b>{PUNISHMENT}</b> ⛨\n\n<white>You have been banned from this server!\n\n{COLOR_LIGHT}⚠ Reason: <white>{REASON}\n{COLOR_LIGHT}⌚ Expires: <white>{EXPIRES}",
        val kicked: String = "{COLOR}⛨ <b>{PUNISHMENT}</b> ⛨\n\n<white>You have been kicked from this server!\n\n{COLOR_LIGHT}⚠ Reason: <white>{REASON}",
        val muted: String = "\n\n {COLOR}⛨ <b>{PUNISHMENT}</b> ⛨\n\n <white>You have been muted from talking in chat!\n\n {COLOR_LIGHT}⚠ Reason: <white>{REASON}\n {COLOR_LIGHT}⌚ Expires: <white>{EXPIRES}\n\n ",
        val punishedStaffMessage: String = "{COLOR}⛨ <dark_gray>| <gray>Player {COLOR_LIGHT}{PLAYER} <gray>has been punished, type: <yellow>{TYPE}<gray>, reason: <white>{REASON}<gray>. Expires: <white>{EXPIRES}",
        val bannedPlayerTriedToJoin: String = "{COLOR}⛨ <dark_gray>| <gray>Banned player ({COLOR_LIGHT}{PLAYER}<gray>) tried to join",
        val mutedPlayerTriedToTalk: String = "{COLOR}⛨ <dark_gray>| <gray>Muted player ({COLOR_LIGHT}{PLAYER}<gray>) tried to talk: <white>{MESSAGE}",
    ) {
        companion object {
            val CODEC = Codec.of(
                "banned", Codecs.String, Messages::banned,
                "kicked", Codecs.String, Messages::kicked,
                "muted", Codecs.String, Messages::muted,
                "punished_staff_message", Codecs.String, Messages::punishedStaffMessage,
                "banned_player_tried_to_join", Codecs.String, Messages::bannedPlayerTriedToJoin,
                "muted_player_tried_to_talk", Codecs.String, Messages::mutedPlayerTriedToTalk,
                ::Messages
            )
        }

        private fun getReplacedMessage(string: String): String {
            return string
                .replace("{PREFIX}", PREFIX)
                .replace("{COLOR}", COLOR)
                .replace("{COLOR_LIGHT}", COLOR_LIGHT)
        }

        fun getBannedMessage(punishment: Punishment): String {
            return getReplacedMessage(banned)
                .replace("{REASON}", punishment.reason)
                .replace("{PUNISHMENT}", punishment.type.past)
                .replace("{EXPIRES}", punishment.expires?.toLocalDateTime()?.formatted() ?: "Never")
        }

        fun getMutedMessage(punishment: Punishment): String {
            return getReplacedMessage(muted)
                .replace("{REASON}", punishment.reason)
                .replace("{PUNISHMENT}", punishment.type.past)
                .replace("{EXPIRES}", punishment.expires?.toLocalDateTime()?.formatted() ?: "Never")
        }

        fun getKickedMessage(punishment: Punishment): String {
            return getReplacedMessage(kicked)
                .replace("{REASON}", punishment.reason)
                .replace("{PUNISHMENT}", punishment.type.past)
        }

        fun getStaffPunishmentMessage(punishment: Punishment, playerName: String): String {
            return getReplacedMessage(punishedStaffMessage)
                .replace("{REASON}", punishment.reason)
                .replace("{PLAYER}", playerName)
                .replace("{TYPE}", punishment.type.name)
                .replace("{PUNISHMENT}", punishment.type.past)
                .replace("{EXPIRES}", punishment.expires?.toLocalDateTime()?.formatted() ?: "Never")
        }

        fun getBannedPlayerTriedToSpeakMessage(punishment: Punishment, playerName: String): String {
            return getReplacedMessage(bannedPlayerTriedToJoin)
                .replace("{REASON}", punishment.reason)
                .replace("{PLAYER}", playerName)
                .replace("{TYPE}", punishment.type.name)
                .replace("{PUNISHMENT}", punishment.type.past)
                .replace("{EXPIRES}", punishment.expires?.toLocalDateTime()?.formatted() ?: "Never")
        }


        fun getMutedPlayerTriedToSpeakMessage(punishment: Punishment, playerName: String, message: String): String {
            return getReplacedMessage(mutedPlayerTriedToTalk)
                .replace("{REASON}", punishment.reason)
                .replace("{PLAYER}", playerName)
                .replace("{TYPE}", punishment.type.name)
                .replace("{PUNISHMENT}", punishment.type.past)
                .replace("{MESSAGE}", message)
                .replace("{EXPIRES}", punishment.expires?.toLocalDateTime()?.formatted() ?: "Never")
        }
    }
}