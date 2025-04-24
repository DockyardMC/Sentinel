package io.github.dockyardmc.sentinel.dockyard

import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.scheduler.runAsync
import io.github.dockyardmc.sentinel.common.platform.SentinelPlayer
import io.github.dockyardmc.utils.MojangUtil
import java.util.concurrent.CompletableFuture

fun getOrFetchSentinelPlayer(username: String): CompletableFuture<SentinelPlayer?> {
    val future = CompletableFuture<SentinelPlayer?>()
    runAsync {
        val localPlayer = PlayerManager.getPlayerByUsernameOrNull(username)
        if (localPlayer != null) {
            future.complete(localPlayer.toSentinelPlayer())
        } else {
            val uuid = MojangUtil.getUUIDFromUsername(username)
            if (uuid == null) {
                future.complete(null)
                return@runAsync
            }
            future.complete(SentinelPlayer(uuid, username))
        }

    }
    return future
}

fun Player.toSentinelPlayer(): SentinelPlayer {
    return SentinelPlayer(this.uuid, this.username)
}

