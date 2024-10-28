package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Settings
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendComponent
import ir.taher7.melodymine.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Call : SubCommand() {
    override var name = "call"
    override var description = Messages.getMessage("commands.call.description")
    override var syntax = "/melodymine call"
    override var permission = "melodymine.call"
    override fun handler(player: CommandSender, args: Array<out String>) {
        if (player !is Player) {
            player.sendComponent(Messages.getMessage("errors.only_players"))
            return
        }
        if (Utils.checkPlayerCoolDown(player)) return

        if (args.size > 3 || args.size < 2) {
            sendCallHelpMessage(player)
            return
        }

        when (args[1]) {

            "start" -> {

                if (!player.hasPermission("melodymine.call.start")) {
                    player.sendComponent(Messages.getMessage("errors.no_permission"))
                    return
                }

                if (args.size != 3) {
                    sendCallHelpMessage(player)
                    return
                }

                val bukkitPlayer = Bukkit.getPlayer(args[2])
                if (bukkitPlayer == null) {
                    player.sendComponent(Messages.getMessage("errors.player_not_found"))
                    return
                }

                if (bukkitPlayer.uniqueId == player.uniqueId) {
                    player.sendComponent(Messages.getMessage("commands.call.call_to_yourself"))
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
                if (!melodyPlayer.isActiveVoice) {
                    player.sendComponent(Messages.getMessage("errors.active_voice"))
                    return
                }

                if (melodyPlayer.isInCall) {
                    player.sendComponent(
                        Messages.getMessage(
                            "commands.call.in_call_start",
                            hashMapOf("{PLAYER}" to melodyPlayer.callTarget?.name!!)
                        )
                    )
                    return
                }

                if (melodyPlayer.isCallPending) {
                    player.sendComponent(
                        Messages.getMessage(
                            "commands.call.in_pending",
                            hashMapOf("{PLAYER}" to melodyPlayer.callPendingTarget?.name!!)
                        )
                    )
                    return
                }

                if (melodyPlayer.callToggle) {
                    player.sendComponent(Messages.getMessage("commands.call.is_toggle"))
                    return
                }

                if (melodyPlayer.adminMode) {
                    player.sendComponent(Messages.getMessage("commands.call.disable_adminmode"))
                    return
                }

                if (Settings.disableWorlds.contains(player.world.name)) {
                    player.sendComponent(Messages.getMessage("commands.call.disable_world"))
                    return
                }

                val targetPlayer = Storage.onlinePlayers[bukkitPlayer.uniqueId.toString()] ?: return
                if (!targetPlayer.isActiveVoice || targetPlayer.isInCall || targetPlayer.isCallPending || targetPlayer.callToggle || Settings.disableWorlds.contains(
                        targetPlayer.player?.world?.name
                    )
                ) {
                    player.sendComponent(
                        Messages.getMessage(
                            "commands.call.not_available",
                            hashMapOf("{PLAYER}" to targetPlayer.name)
                        )
                    )
                    return
                }

                MelodyManager.startCall(melodyPlayer, targetPlayer)
                Utils.resetPlayerCoolDown(player)
                return
            }

            "end" -> {
                if (!player.hasPermission("melodymine.call.end")) {
                    player.sendComponent(Messages.getMessage("errors.no_permission"))
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
                if (!melodyPlayer.isActiveVoice) {
                    player.sendComponent(Messages.getMessage("errors.active_voice"))
                    return
                }

                if (!melodyPlayer.isInCall) {
                    player.sendComponent(Messages.getMessage("commands.call.not_in_call"))
                    return
                }

                if (melodyPlayer.adminMode) {
                    player.sendComponent(Messages.getMessage("commands.call.disable_adminmode"))
                    return
                }

                val targetPlayer = Storage.onlinePlayers[melodyPlayer.callTarget?.uuid] ?: return
                if (!targetPlayer.isActiveVoice || !targetPlayer.isInCall || targetPlayer.isCallPending) {
                    player.sendComponent(
                        Messages.getMessage(
                            "commands.call.not_available",
                            hashMapOf("{PLAYER}" to targetPlayer.name)
                        )
                    )
                    return
                }

                MelodyManager.endCall(melodyPlayer, targetPlayer)

                Utils.resetPlayerCoolDown(player)
                return
            }

            "accept" -> {
                if (!player.hasPermission("melodymine.call.accept")) {
                    player.sendComponent(Messages.getMessage("errors.no_permission"))
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
                if (!melodyPlayer.isActiveVoice) {
                    player.sendComponent(Messages.getMessage("errors.active_voice"))
                    return
                }

                if (melodyPlayer.isInCall) {
                    player.sendComponent(
                        Messages.getMessage(
                            "commands.call.in_call_accept",
                            hashMapOf("{PLAYER}" to melodyPlayer.callTarget?.name!!)
                        )
                    )
                    return
                }

                if (!melodyPlayer.isCallPending) {
                    player.sendComponent(Messages.getMessage("commands.call.call_request"))
                    return
                }

                if (melodyPlayer.callToggle) {
                    player.sendComponent(Messages.getMessage("commands.call.is_toggle"))
                    return
                }

                if (melodyPlayer.adminMode) {
                    player.sendComponent(Messages.getMessage("commands.call.disable_adminmode"))
                    return
                }

                if (Settings.disableWorlds.contains(player.world.name)) {
                    player.sendComponent(Messages.getMessage("commands.call.disable_world"))
                    return
                }

                val targetPlayer = Storage.onlinePlayers[melodyPlayer.callPendingTarget?.uuid] ?: return
                if (!targetPlayer.isActiveVoice || targetPlayer.isInCall || !targetPlayer.isCallPending || targetPlayer.callToggle || targetPlayer.adminMode || Settings.disableWorlds.contains(
                        targetPlayer.player?.world?.name
                    )
                ) {
                    player.sendComponent(
                        Messages.getMessage(
                            "commands.call.not_available",
                            hashMapOf("{PLAYER}" to targetPlayer.name)
                        )
                    )
                    return
                }

                MelodyManager.acceptCall(melodyPlayer, targetPlayer)

                Utils.resetPlayerCoolDown(player)
                return
            }

            "deny" -> {
                if (!player.hasPermission("melodymine.call.deny")) {
                    player.sendComponent(Messages.getMessage("errors.no_permission"))
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
                if (!melodyPlayer.isActiveVoice) {
                    player.sendComponent(Messages.getMessage("errors.active_voice"))
                    return
                }

                if (melodyPlayer.isInCall) {
                    player.sendComponent(
                        Messages.getMessage(
                            "commands.call.in_call_start",
                            hashMapOf("{PLAYER}" to melodyPlayer.callTarget?.name!!)
                        )
                    )
                    return
                }

                if (!melodyPlayer.isCallPending) {
                    player.sendComponent(Messages.getMessage("commands.call.call_request"))
                    return
                }

                if (melodyPlayer.callToggle) {
                    player.sendComponent(Messages.getMessage("commands.call.is_toggle"))
                    return
                }

                val targetPlayer = Storage.onlinePlayers[melodyPlayer.callPendingTarget?.uuid] ?: return
                if (!targetPlayer.isActiveVoice || targetPlayer.isInCall || !targetPlayer.isCallPending || targetPlayer.callToggle) {
                    player.sendComponent(
                        Messages.getMessage(
                            "commands.call.not_available",
                            hashMapOf("{PLAYER}" to targetPlayer.name)
                        )
                    )
                    return
                }

                MelodyManager.denyCall(melodyPlayer, targetPlayer)

                Utils.resetPlayerCoolDown(player)
                return
            }

            "toggle" -> {
                if (!player.hasPermission("melodymine.call.toggle")) {
                    player.sendComponent(Messages.getMessage("errors.no_permission"))
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return

                MelodyManager.toggleCall(melodyPlayer)
                Utils.resetPlayerCoolDown(player)
                return
            }

            else -> sendCallHelpMessage(player)
        }

    }

    private fun sendCallHelpMessage(player: Player) {
        player.sendComponent(Messages.getMessage("general.content_header"))
        Messages.getHelpMessage(
            "commands.call.help_message",
            hashMapOf("{SYNTAX}" to syntax)
        ).forEach { message ->
            player.sendComponent(message)
        }
        player.sendComponent(Messages.getMessage("general.content_footer"))
    }
}
