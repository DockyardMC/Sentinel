package io.github.dockyardmc.sentinel.common.punishment

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.dockyard.cz.lukynka.hollow.HollowCache
import io.github.dockyardmc.sentinel.common.platform.SentinelPlayer

object PlayerPunishmentDataCache : HollowCache<PlayerPunishmentData>("player_punishment_data") {

    fun getOrCreate(player: SentinelPlayer): PlayerPunishmentData {
        var punishmentData = PlayerPunishmentDataCache.getOrNull(player.uuid)

        // create new entry in the database if it doesn't exist
        if (punishmentData == null) {
            punishmentData = PlayerPunishmentData(player.uuid, player.username, mutableListOf())
            PlayerPunishmentDataCache[player.uuid] = punishmentData
        }

        // update last known name if it doesn't match
        if(punishmentData.lastKnownUsername != player.username) {
            punishmentData.lastKnownUsername = player.username
            PlayerPunishmentDataCache[player.uuid] = punishmentData
        }

        return punishmentData
    }

    override fun serialize(value: PlayerPunishmentData): String {
        val json = JsonObject()
        PlayerPunishmentData.CODEC.writeJson(json, value)
        return json.toString()
    }

    override fun deserialize(string: String): PlayerPunishmentData {
        val json = JsonParser.parseString(string)
        return PlayerPunishmentData.CODEC.readJson(json)
    }

}