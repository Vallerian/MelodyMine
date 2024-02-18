package ir.taher7.melodymine.api.events


import ir.taher7.melodymine.models.RenewConnectionData
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PostRenewConnectionDataEvent(val data: List<RenewConnectionData>) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}