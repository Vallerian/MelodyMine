package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendComponent
import ir.taher7.melodymine.utils.Utils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Control : SubCommand() {
    override var name = "control"
    override var description = Messages.getMessage("commands.control.description")
    override var syntax = "/melodymine control"
    override var permission = "melodymine.control"
    override fun handler(player: CommandSender, args: Array<out String>) {
        if (player !is Player) {
            player.sendComponent(Messages.getMessage("errors.only_players"))
            return
        }
        if (Utils.checkPlayerCoolDown(player)) return

        if (args.size != 2) {
            sendControlHelpMessage(player)
            return
        }

        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
        if (!melodyPlayer.isActiveVoice) {
            player.sendComponent(Messages.getMessage("errors.active_voice"))
            return
        }

        if (args[1].equals("mute", true)) {
            MelodyManager.setPlayerSelfMute(melodyPlayer, !melodyPlayer.isSelfMute)
            if (melodyPlayer.isSelfMute) {
                player.sendComponent(Messages.getMessage("commands.control.unmute"))
            } else {
                player.sendComponent(Messages.getMessage("commands.control.mute"))
            }
            Utils.resetPlayerCoolDown(player)
            return
        }

        if (args[1].equals("deafen", true)) {
            MelodyManager.setPlayerDeafen(melodyPlayer, !melodyPlayer.isDeafen)
            if (melodyPlayer.isDeafen) {
                player.sendComponent(Messages.getMessage("commands.control.undeafen"))
            } else {
                player.sendComponent(Messages.getMessage("commands.control.deafen"))
            }
            Utils.resetPlayerCoolDown(player)
            return
        }

        sendControlHelpMessage(player)
    }

    private fun sendControlHelpMessage(player: Player) {
        player.sendComponent(Messages.getMessage("general.content_header"))
        Messages.getHelpMessage(
            "commands.control.help_message",
            hashMapOf("{SYNTAX}" to syntax)
        ).forEach { message ->
            player.sendComponent(message)
        }
        player.sendComponent(Messages.getMessage("general.content_footer"))
    }
}
