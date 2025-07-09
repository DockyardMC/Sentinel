package io.github.dockyardmc.sentinel.common.punishment

import cz.lukynka.hollow.RealmStorage
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmUUID
import java.util.*

object PunishmentDataStorage : RealmStorage<PlayerPunishmentData>(PlayerPunishmentData::class) {

    fun getOrCreate(uuid: UUID): PlayerPunishmentData {
        var data = PunishmentDataStorage.firstOrNull { and { equals("uuid", RealmUUID.from(uuid.toString())) } }

        if (data == null) {
            data = PlayerPunishmentData(RealmUUID.from(uuid.toString()), "", realmListOf())
        }
        return data
    }

}