package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Utils
import org.bukkit.entity.Player

class Unmute : SubCommand() {
    override var name = "unmute"
    override var description = Messages.getMessageString("commands.unmute.description")
    override var syntax = "/melodymine unmute"
    override var permission = "melodymine.unmute"
    override fun handler(player: Player, args: Array<out String>) {
        if (Utils.checkPlayerCoolDown(player)) return

        if (args.size != 2) {
            sendStartHelpMessage(player)
            return
        }
        val filter = Storage.onlinePlayers.values.filter { melodyPlayer ->
            melodyPlayer.name.equals(args[1], true)
        }
        if (filter.isEmpty()) {
            player.sendMessage(Messages.getMessage("errors.player_not_found"))
            return
        }
        val targetPlayer = filter[0]
        if (!targetPlayer.isMute) {
            player.sendMessage(
                Messages.getMessage(
                    "commands.unmute.not_mute",
                    hashMapOf("{PLAYER}" to targetPlayer.name)
                )
            )
            return
        }

        MelodyManager.unMute(targetPlayer.uuid)
        targetPlayer.player?.sendMessage(Messages.getMessage("commands.unmute.success_target"))
        player.sendMessage(
            Messages.getMessage(
                "commands.unmute.success",
                hashMapOf("{PLAYER}" to targetPlayer.name)
            )
        )
        Utils.resetPlayerCoolDown(player)
    }

    private fun sendStartHelpMessage(player: Player) {
        player.sendMessage(Messages.getMessage("general.content_header"))
        Messages.getHelpMessage(
            "commands.unmute.help_message",
            hashMapOf("{SYNTAX}" to syntax)
        ).forEach { message ->
            player.sendMessage(message)
        }
        player.sendMessage(Messages.getMessage("general.content_footer"))
    }
}
