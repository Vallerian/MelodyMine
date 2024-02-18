package ir.taher7.melodymine.api.events

import ir.taher7.melodymine.models.RenewDistanceData
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PostRenewDistanceDataEvent (val data: List<RenewDistanceData>) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}