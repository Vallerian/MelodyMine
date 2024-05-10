package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Mute : SubCommand() {
    override var name = "mute"
    override var description = Messages.getMessageString("commands.mute.description")
    override var syntax = "/melodymine mute"
    override var permission = "melodymine.mute"
    override fun handler(player: Player, args: Array<out String>) {
        if (Utils.checkPlayerCoolDown(player)) return

        if (args.size != 2) {
            sendStartHelpMessage(player)
            return
        }

        val targetPlayer = Bukkit.getPlayer(args[1])
        if (targetPlayer == null) {
            player.sendMessage(Messages.getMessage("errors.player_not_found"))
            return
        }

        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
        if (melodyPlayer.isMute) {
            player.sendMessage(
                Messages.getMessage(
                    "commands.mute.already_mute",
                    hashMapOf("{PLAYER}" to melodyPlayer.name)
                )
            )
            return
        }

        MelodyManager.mute(targetPlayer.uniqueId.toString())

        targetPlayer.player?.sendMessage(Messages.getMessage("commands.mute.success_target"))
        player.sendMessage(
            Messages.getMessage(
                "commands.mute.success",
                hashMapOf("{PLAYER}" to targetPlayer.name)
            )
        )
        Utils.resetPlayerCoolDown(player)
    }

    private fun sendStartHelpMessage(player: Player) {
        player.sendMessage(Messages.getMessage("general.content_header"))
        Messages.getHelpMessage(
            "commands.mute.help_message",
            hashMapOf("{SYNTAX}" to syntax)
        ).forEach { message ->
            player.sendMessage(message)
        }
        player.sendMessage(Messages.getMessage("general.content_footer"))
    }

}