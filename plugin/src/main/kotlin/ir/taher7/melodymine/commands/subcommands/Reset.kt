package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Reset : SubCommand() {

    override var name = "reset"
    override var description = Messages.getMessage("commands.reset.description")
    override var syntax = "/melodymine reset"
    override var permission = "melodymine.reset"

    override fun handler(player: Player, args: Array<out String>) {
        if (args.size != 2) {
            sendResetHelpMessage(player)
            return
        }

        Database.resetPlayerData(args[1].lowercase()) { result ->
            if (result) {
                player.sendComponent(
                    Messages.getMessage(
                        "commands.reset.success",
                        hashMapOf("{PLAYER}" to args[1])
                    )
                )
            } else {
                player.sendComponent(Messages.getMessage("errors.player_not_found"))
            }
        }

        val bukkitPlayer = Bukkit.getPlayer(args[1]) ?: return
        Storage.onlinePlayers[bukkitPlayer.uniqueId.toString()]?.webIsOnline = false


    }

    private fun sendResetHelpMessage(player: Player) {
        player.sendComponent(Messages.getMessage("general.content_header"))
        Messages.getHelpMessage(
            "commands.reset.help_message",
            hashMapOf("{SYNTAX}" to syntax)
        ).forEach { message ->
            player.sendComponent(message)
        }
        player.sendComponent(Messages.getMessage("general.content_footer"))
    }

}