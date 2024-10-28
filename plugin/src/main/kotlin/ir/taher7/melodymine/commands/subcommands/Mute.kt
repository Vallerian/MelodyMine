package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendComponent
import ir.taher7.melodymine.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Mute : SubCommand() {
    override var name = "mute"
    override var description = Messages.getMessage("commands.mute.description")
    override var syntax = "/melodymine mute"
    override var permission = "melodymine.mute"
    override fun handler(player: CommandSender, args: Array<out String>) {
        if (player is Player) {
            if (Utils.checkPlayerCoolDown(player)) return
            if (args.size != 2) {
                sendStartHelpMessage(player)
                return
            }
        }

        val targetPlayer = Bukkit.getPlayer(args[1])
        if (targetPlayer == null) {
            player.sendComponent(Messages.getMessage("errors.player_not_found"))
            return
        }

        val melodyPlayer = Storage.onlinePlayers[targetPlayer.uniqueId.toString()] ?: return

        if (melodyPlayer.isMute) {
            player.sendComponent(
                Messages.getMessage(
                    "commands.mute.already_mute",
                    hashMapOf("{PLAYER}" to melodyPlayer.name)
                )
            )
            return
        }

        MelodyManager.mute(targetPlayer.uniqueId.toString())

        targetPlayer.player?.sendComponent(Messages.getMessage("commands.mute.success_target"))
        player.sendComponent(
            Messages.getMessage(
                "commands.mute.success",
                hashMapOf("{PLAYER}" to targetPlayer.name)
            )
        )

        if (player is Player) Utils.resetPlayerCoolDown(player)
    }

    private fun sendStartHelpMessage(player: Player) {
        player.sendComponent(Messages.getMessage("general.content_header"))
        Messages.getHelpMessage(
            "commands.mute.help_message",
            hashMapOf("{SYNTAX}" to syntax)
        ).forEach { message ->
            player.sendComponent(message)
        }
        player.sendComponent(Messages.getMessage("general.content_footer"))
    }
}
