package ir.taher7.melodymine.commands.subcommands


import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.entity.Player
import java.util.*

class AdminMode : SubCommand() {

    private val coolDown = hashMapOf<UUID, Long>()

    override var name = "adminmode"
    override var description = Storage.adminmodeDescription
    override var syntax = "/melodymine adminmode"
    override var permission = "melodymine.adminmode"

    override fun handler(player: Player, args: Array<out String>) {
        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return

        if (coolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - coolDown[player.uniqueId]!!) <= 5000) {
            player.sendMessage("<prefix>You can use this command after <count_color>${((5000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)} <text>second.".toComponent())
            return
        }

        if (!melodyPlayer.webIsOnline) {
            player.sendMessage("<prefix>To start Admin mode you need to connect the website.".toComponent())
            return
        }

        if (!melodyPlayer.isActiveVoice) {
            player.sendMessage("<prefix>To start Admin mode you need to click on the start melody in the website.".toComponent())
            return
        }

        if (melodyPlayer.adminMode) {
            MelodyManager.disableAdminMode(melodyPlayer.uuid)
            player.sendMessage("<prefix>Admin mode has been disabled.".toComponent())
        } else {
            MelodyManager.enableAdminMode(melodyPlayer.uuid)
            player.sendMessage("<prefix>Admin mode has been enabled.".toComponent())
        }
        coolDown[player.uniqueId] = System.currentTimeMillis()
    }
}