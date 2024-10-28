package ir.taher7.melodymine.api.events


import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.storage.Storage
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PrePlaySoundEvent(
    val soundName: String,
    val sendToAll: Boolean,
    val socketID: String?,
    val volume: Double?,
) : Event(), Cancellable {
    private var cancelled = false

    fun getPlayer(): MelodyPlayer? {
        return Storage.onlinePlayers.values.find { online -> online.socketID == socketID }
    }

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
