package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Reset : SubCommand() {

    override var name = "reset"
    override var description = "Reset Player Storage Data."
    override var syntax = "/melodymine reset"
    override var permission = "melodymine.reset"

    override fun handler(player: Player, args: Array<out String>) {
        if (args[1].isEmpty()) {
            sendResetHelpMessage(player)
            return
        }

        Database.resetPlayerData(args[1].lowercase()) { result ->
            if (result) {
                player.sendMessage("<prefix><count_color>${args[1]} <text>Storage Data has been Reset.".toComponent())
            } else {
                player.sendMessage("<prefix><count_color>${args[1]} <text>not Found.".toComponent())
            }
        }

        val bukkitPlayer = Bukkit.getPlayer(args[1]) ?: return
        Storage.onlinePlayers[bukkitPlayer.uniqueId.toString()]?.webIsOnline = false


    }

    private fun sendResetHelpMessage(player: Player) {
        player.sendMessage(Storage.contentHeader.toComponent())
        player.sendMessage("")
        player.sendMessage("<text_hover>${syntax} <player> <#FFF4E4><bold>|</bold> <text>Reset Player Storage Data.".toComponent())
        player.sendMessage("")
        player.sendMessage(Storage.contentFooter.toComponent())
    }

}