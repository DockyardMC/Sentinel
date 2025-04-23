package io.github.dockyardmc.sentinel.dockyard

import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.scheduler.runAsync
import io.github.dockyardmc.utils.MojangUtil
import kotlinx.datetime.Instant
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun getOrFetchPlayerUUID(username: String): CompletableFuture<UUID?> {
    val future = CompletableFuture<UUID?>()
    runAsync {
        val localPlayer = PlayerManager.getPlayerByUsernameOrNull(username)
        if (localPlayer != null) {
            future.complete(localPlayer.uuid)
        } else {
            future.complete(MojangUtil.getUUIDFromUsername(username))
        }

    }
    return future
}

