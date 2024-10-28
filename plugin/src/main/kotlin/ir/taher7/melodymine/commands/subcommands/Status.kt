package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendComponent
import ir.taher7.melodymine.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Status : SubCommand() {
    override var name = "status"
    override var description = Messages.getMessage("commands.status.description")
    override var syntax = "/melodymine status"
    override var permission = "melodymine.status"

    override fun handler(player: CommandSender, args: Array<out String>) {
        if (player !is Player) {
            player.sendComponent(Messages.getMessage("errors.only_players"))
            return
        }

        if (Utils.checkPlayerCoolDown(player)) return
        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return

        if (args.size == 1) {
            if (!melodyPlayer.isActiveVoice) {
                player.sendComponent(Messages.getMessage("errors.active_voice"))
                return
            }

            sendStatus(player, melodyPlayer)

            Utils.resetPlayerCoolDown(player)
            return
        }

        if (args.size == 2) {
            if (!player.hasPermission("melodymine.status.others")) {
                player.sendComponent(Messages.getMessage("errors.no_permission"))
                return
            }

            val targetMelodyPlayer = Bukkit.getPlayer(args[1])
            if (targetMelodyPlayer == null) {
                player.sendComponent(Messages.getMessage("errors.player_not_found"))
                return
            }

            val melodyTargetPlayer = Storage.onlinePlayers[targetMelodyPlayer.uniqueId.toString()]
            if (melodyTargetPlayer == null) {
                player.sendComponent(Messages.getMessage("errors.player_not_found"))
                return
            }

            if (!melodyTargetPlayer.isActiveVoice) {
                player.sendComponent(
                    Messages.getMessage(
                        "errors.active_voice_others",
                        hashMapOf("{PLAYER}" to melodyTargetPlayer.name)
                    )
                )
                return
            }

            sendStatus(player, melodyTargetPlayer)
            Utils.resetPlayerCoolDown(player)
            return
        }

        sendStatusHelpMessage(player)
        Utils.resetPlayerCoolDown(player)
    }

    private fun sendStatus(player: Player, melodyPlayer: MelodyPlayer) {
        player.sendComponent(Messages.getMessage("general.content_header"))
        player.sendComponent("")
        player.sendComponent("<prefix><highlight_color>${melodyPlayer.name} <text_color>Voice Status:")
        player.sendComponent("<prefix>Activate: <highlight_color>Online<text_color>.")
        player.sendComponent("<prefix>Microphone: <highlight_color>${if (!melodyPlayer.isSelfMute) "Mute" else "Active"}<text_color>.")
        player.sendComponent("<prefix>Speaker: <highlight_color>${if (!melodyPlayer.isDeafen) "Deafen" else "Active"}<text_color>.")
        player.sendComponent("<prefix>Call: <hover_color>${if (melodyPlayer.isInCall) "You're In Call With <highlight_color>${melodyPlayer.callTarget?.name}" else "<highlight_color>OFF"}<text_color>.")
        player.sendComponent(
            "<prefix>Voice Connections: <highlight_color>${melodyPlayer.isSendOffer.size}<text_color>, Players: <highlight_color>${
                melodyPlayer.isSendOffer.map { uuid -> Storage.onlinePlayers[uuid]?.name }
                    .joinToString("<text_color>, <highlight_color>")
            }<text_color>."
        )
        player.sendComponent("")
        player.sendComponent(Messages.getMessage("general.content_footer"))
    }

    private fun sendStatusHelpMessage(player: Player) {
        player.sendComponent(Messages.getMessage("general.content_header"))
        Messages.getHelpMessage(
            "commands.status.help_message",
            hashMapOf("{SYNTAX}" to syntax)
        ).forEach { message ->
            player.sendComponent(message)
        }
        player.sendComponent(Messages.getMessage("general.content_footer"))
    }
}
