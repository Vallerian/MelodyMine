package ir.taher7.melodymine.api.events


import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.models.MelodyTalk
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerChangeTalkEvent(
    val melodyPlayer: MelodyPlayer,
    val melodyTalk: MelodyTalk,
) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}
