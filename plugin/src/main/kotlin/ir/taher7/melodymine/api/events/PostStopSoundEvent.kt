package ir.taher7.melodymine.api.events

import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.storage.Storage
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PostStopSoundEvent(val soundName: String, val sendToAll: Boolean, val socketID: String?) : Event() {
    fun getPlayer(): MelodyPlayer? {
        return Storage.onlinePlayers.values.find { online -> online.socketID == socketID }
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
