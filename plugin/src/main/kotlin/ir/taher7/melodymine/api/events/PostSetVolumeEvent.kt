package ir.taher7.melodymine.api.events

import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.storage.Storage
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PostSetVolumeEvent(
    val playerUuid: String,
    val targetSocketID: String,
) : Event() {
    fun getPlayer(): MelodyPlayer? {
        return Storage.onlinePlayers[playerUuid]
    }

    fun getTargetPlayer(): MelodyPlayer? {
        return Storage.onlinePlayers.values.find { online -> online.socketID == targetSocketID }
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
