package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.entity.Player
import java.util.*

class Toggle : SubCommand() {

    private val coolDown = hashMapOf<UUID, Long>()

    override var name = "toggle"
    override var description = Storage.toggleDescription
    override var syntax = "/melodymine toggle"
    override var permission = "melodymine.toggle"

    override fun handler(player: Player, args: Array<out String>) {

        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
        if (coolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - coolDown[player.uniqueId]!!) <= 5000) {
            player.sendMessage("<prefix>You can use this command after <count_color>${((5000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)}</count_color> second.".toComponent())
            return
        }

        if (melodyPlayer.isToggle) {
            MelodyManager.disableLogger(melodyPlayer.uuid)
            player.sendMessage("<prefix>Message log has been disabled.".toComponent())
        } else {
            MelodyManager.enableLogger(melodyPlayer.uuid)
            player.sendMessage("<prefix>Message log has been enabled.".toComponent())
        }
        coolDown[player.uniqueId] = System.currentTimeMillis()
    }
}