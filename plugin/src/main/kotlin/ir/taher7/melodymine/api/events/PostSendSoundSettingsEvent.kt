package ir.taher7.melodymine.api.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PostSendSoundSettingsEvent(val socketID: String) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
