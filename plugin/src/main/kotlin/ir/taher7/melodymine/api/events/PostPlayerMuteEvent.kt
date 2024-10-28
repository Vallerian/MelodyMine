package ir.taher7.melodymine.api.events

import ir.taher7.melodymine.models.MelodyPlayer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PostPlayerMuteEvent(val melodyPlayer: MelodyPlayer) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
