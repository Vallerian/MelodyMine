package ir.taher7.melodymine.listeners

import ir.taher7.melodymine.storage.Storage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent


class NameTagListener : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!Storage.isEnableNameTag) return
        val to = event.to ?: return
        val from = event.from
        if (from.x == to.x && from.y == to.y && from.z == to.z) return
        val melodyPlayer = Storage.onlinePlayers[event.player.uniqueId.toString()] ?: return
        event.to?.let { melodyPlayer.talkNameTag?.moveNameTag(event.from, it) }
    }

}