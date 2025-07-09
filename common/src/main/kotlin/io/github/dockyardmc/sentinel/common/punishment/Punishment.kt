package io.github.dockyardmc.sentinel.common.punishment

import cz.lukynka.hollow.RealmEnum
import io.github.dockyardmc.sentinel.common.utils.FriendlyLocalDateTime
import io.realm.kotlin.types.RealmObject

class Punishment(
    private var realmType: RealmEnum?,
    var expires: FriendlyLocalDateTime?,
    var received: FriendlyLocalDateTime?,
    var reason: String,
    var punisher: String,
    var active: Boolean = true,
    var acknowledgementString: String? = null,
) : RealmObject {

    var type
        get() = realmType!!.getVal<Type>()
        set(value) {
            realmType = RealmEnum.of(value)
        }

    constructor() : this(
        realmType = RealmEnum.of(Type.BAN),
        expires = FriendlyLocalDateTime(0),
        received = FriendlyLocalDateTime(0),
        reason = "",
        punisher = "",
        active = false,
        acknowledgementString = null
    )

    enum class Type(val kicksPlayer: Boolean, val past: String) {
        BAN(true, "BANNED"),
        KICK(true, "KICKED"),
        MUTE(false, "MUTED"),
        WARN(false, "WARNED"),
    }
}