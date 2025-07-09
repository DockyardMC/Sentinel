package io.github.dockyardmc.sentinel.common.punishment

import cz.lukynka.hollow.EMPTY
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey

class PlayerPunishmentData(
    @PrimaryKey
    var uuid: RealmUUID,
    var lastKnownUsername: String,
    var punishments: RealmList<Punishment>,
) : RealmObject {
    constructor() : this(RealmUUID.EMPTY, "", realmListOf())
}