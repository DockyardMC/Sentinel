package io.github.dockyardmc.sentinel.common.punishment

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.dockyard.cz.lukynka.hollow.HollowCache
import java.util.*

object PlayerPunishmentDataCache : HollowCache<PlayerPunishmentData>("player_punishment_data") {

    fun getOrCreate(key: UUID): PlayerPunishmentData {
        var punishmentData = PlayerPunishmentDataCache.getOrNull(key)
        if (punishmentData == null) {
            punishmentData = PlayerPunishmentData(key, mutableListOf())
            PlayerPunishmentDataCache[key] = punishmentData
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