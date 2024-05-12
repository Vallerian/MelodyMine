package ir.taher7.melodymine.api.events

import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.models.MelodyPlayer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PostChangeSoundVolumeEvent (
    val soundName: String,
    val sendToAll: Boolean,
    val socketID: String?,
    val volume: Double?,
) : Event() {

    fun getPlayer(): MelodyPlayer? {
        return socketID?.let { MelodyManager.getMelodyPlayerFromSocketID(it) }
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}