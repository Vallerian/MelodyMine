package ir.taher7.melodymine.api.events

import ir.taher7.melodymine.models.RenewData
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PostRenewDataEvent(val data: RenewData) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
