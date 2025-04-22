package io.github.dockyardmc.sentinel.common.messages

import io.github.dockyardmc.tide.Codec
import io.github.dockyardmc.tide.Codecs
import io.github.dockyardmc.tide.transcoders.Toml
import io.github.dockyardmc.tide.transcoders.TomlTranscoder
import java.io.File

object SentinelMessagesConfig {

    const val COLOR = "<#a83244>"
    const val COLOR_LIGHT = "<#ff6670>"
    const val PREFIX = "$COLOR⛨ <b>SENTINEL</b> ⛨"

    val FILE = File("./sentinel.toml")

    fun initialize() {
        if(!FILE.exists()) {
            val toml = Toml()
            Messages.CODEC.writeTranscoded(TomlTranscoder, toml, Messages(), "")

            FILE.createNewFile()
            FILE.writeText(toml.getAsString())
        }
    }

    data class Messages(
        val banned: String = "{PREFIX}\n\n<white>You have been banned!",
        val reasonField: String = "{COLOR_LIGHT}▶ <gray>Reason: <white>{REASON}",
        val expiresField: String = "{COLOR_LIGHT}⌚ <gray>Expires: <white>{EXPIRES}",
    ) {
        companion object {
            val CODEC = Codec.of<Messages> {
                field("banned", Codecs.String, Messages::banned)
                field("reason_field", Codecs.String, Messages::reasonField)
                field("expires_field", Codecs.String, Messages::expiresField)
            }
        }
    }
}