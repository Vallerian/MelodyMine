package ir.taher7.melodymine.listeners

import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Utils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class CallListener : Listener {
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val melodyPlayer = Storage.onlinePlayers[event.player.uniqueId.toString()] ?: return
        Utils.clearUpCall(melodyPlayer, true)
    }
}
