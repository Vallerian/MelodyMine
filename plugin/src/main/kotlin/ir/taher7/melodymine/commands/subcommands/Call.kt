package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class Call : SubCommand() {
    private val coolDown = hashMapOf<UUID, Long>()

    override var name = "call"
    override var description = Storage.callDescription
    override var syntax = "/melodymine call"
    override var permission = "melodymine.call"
    override fun handler(player: Player, args: Array<out String>) {
        if (coolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - coolDown[player.uniqueId]!!) <= 5000) {
            player.sendMessage("<prefix>You can use this command after <count_color>${((5000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)}<text> second.".toComponent())
            return
        }

        if (args.size > 3 || args.size < 2) {
            sendCallHelpMessage(player)
            return
        }

        when (args[1]) {

            "start" -> {

                if (!player.hasPermission("melodymine.call.start")) {
                    player.sendMessage("<prefix>You dont have permission to use this command.".toComponent())
                    return
                }

                if (args.size != 3) {
                    sendCallHelpMessage(player)
                    return
                }

                val bukkitPlayer = Bukkit.getPlayer(args[2])
                if (bukkitPlayer == null) {
                    player.sendMessage("<prefix>Player is not online.".toComponent())
                    return
                }

                if (bukkitPlayer.uniqueId == player.uniqueId) {
                    player.sendMessage("<prefix>You Can't Call Yourself.".toComponent())
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
                if (!melodyPlayer.isActiveVoice) {
                    player.sendMessage("<prefix>You must Active your Voice Chat, do <i>/melodymine start link</i> to Start Voice Chat.".toComponent())
                    return
                }

                if (melodyPlayer.isInCall) {
                    player.sendMessage("<prefix>You're Already in Call with<count_color> ${melodyPlayer.callTarget?.name}<text>, if You want to Start new Call do <i>/melodymine call end</i>.".toComponent())
                    return
                }

                if (melodyPlayer.isCallPending) {
                    player.sendMessage("<prefix>You're is Already in Pending Call with<count_color> ${melodyPlayer.callPendingTarget?.name}<text>, Please wait to end the Pending Call.".toComponent())
                    return
                }

                if (melodyPlayer.callToggle) {
                    player.sendMessage("<prefix>Your Call Request is Disable do <i>/melodymine call toggle</i> to Enable Your Call Request.".toComponent())
                    return
                }

                if (melodyPlayer.adminMode) {
                    player.sendMessage("<prefix>You Must First Disable Your AdminMode.".toComponent())
                    return
                }

                val targetPlayer = Storage.onlinePlayers[bukkitPlayer.uniqueId.toString()] ?: return
                if (!targetPlayer.isActiveVoice || targetPlayer.isInCall || targetPlayer.isCallPending || targetPlayer.callToggle) {
                    player.sendMessage("<prefix><count_color>${targetPlayer.name} <text>is not Available Please try Again later.".toComponent())
                    return
                }

                MelodyManager.startCall(melodyPlayer, targetPlayer)
                coolDown[player.uniqueId] = System.currentTimeMillis()
                return
            }

            "end" -> {
                if (!player.hasPermission("melodymine.call.end")) {
                    player.sendMessage("<prefix>You dont have permission to use this command.".toComponent())
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
                if (!melodyPlayer.isActiveVoice) {
                    player.sendMessage("<prefix>You must Active your Voice Chat, do <i>/melodymine start link</i> to Start Voice Chat.".toComponent())
                    return
                }

                if (!melodyPlayer.isInCall) {
                    player.sendMessage("<prefix>You're Not to Any Call.".toComponent())
                    return
                }

                if (melodyPlayer.adminMode) {
                    player.sendMessage("<prefix>You Must First Disable Your AdminMode.".toComponent())
                    return
                }

                val targetPlayer = Storage.onlinePlayers[melodyPlayer.callTarget?.uuid] ?: return
                if (!targetPlayer.isActiveVoice || !targetPlayer.isInCall || targetPlayer.isCallPending) {
                    player.sendMessage("<prefix><count_color>${targetPlayer.name} <text>is not Available Please try Again later.".toComponent())
                    return
                }

                MelodyManager.endCall(melodyPlayer, targetPlayer)

                coolDown[player.uniqueId] = System.currentTimeMillis()
                return
            }

            "accept" -> {
                if (!player.hasPermission("melodymine.call.accept")) {
                    player.sendMessage("<prefix>You dont have permission to use this command.".toComponent())
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
                if (!melodyPlayer.isActiveVoice) {
                    player.sendMessage("<prefix>You must Active your Voice Chat, do <i>/melodymine start link</i> to Start Voice Chat.".toComponent())
                    return
                }

                if (melodyPlayer.isInCall) {
                    player.sendMessage("<prefix>You're Already in Call with<count_color> ${melodyPlayer.callTarget?.name}<text>, if You want to Accept Call do <i>/melodymine call end</i>.".toComponent())
                    return
                }

                if (!melodyPlayer.isCallPending) {
                    player.sendMessage("<prefix>You don't have any Call Request.".toComponent())
                    return
                }

                if (melodyPlayer.callToggle) {
                    player.sendMessage("<prefix>Your Call Request is Disable do <i>/melodymine call toggle</i> to Enable Your Call Request.".toComponent())
                    return
                }

                if (melodyPlayer.adminMode) {
                    player.sendMessage("<prefix>You Must First Disable Your AdminMode.".toComponent())
                    return
                }

                val targetPlayer = Storage.onlinePlayers[melodyPlayer.callPendingTarget?.uuid] ?: return
                if (!targetPlayer.isActiveVoice || targetPlayer.isInCall || !targetPlayer.isCallPending || targetPlayer.callToggle || targetPlayer.adminMode) {
                    player.sendMessage("<prefix><count_color>${targetPlayer.name} <text>is not Available Please try Again later.".toComponent())
                    return
                }

                MelodyManager.acceptCall(melodyPlayer, targetPlayer)

                coolDown[player.uniqueId] = System.currentTimeMillis()
                return
            }

            "deny" -> {
                if (!player.hasPermission("melodymine.call.deny")) {
                    player.sendMessage("<prefix>You dont have permission to use this command.".toComponent())
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
                if (!melodyPlayer.isActiveVoice) {
                    player.sendMessage("<prefix>You must Active your Voice Chat, do <i>/melodymine start link</i> to Start Voice Chat.".toComponent())
                    return
                }

                if (melodyPlayer.isInCall) {
                    player.sendMessage("<prefix>You're Already in Call with<count_color> ${melodyPlayer.callTarget?.name}<text>, if You want to Accept Call do <i>/melodymine call end</i>.".toComponent())
                    return
                }

                if (!melodyPlayer.isCallPending) {
                    player.sendMessage("<prefix>You don't have any Call Request.".toComponent())
                    return
                }

                if (melodyPlayer.callToggle) {
                    player.sendMessage("<prefix>Your Call Request is Disable do <i>/melodymine call toggle</i> to Enable Your Call Request.".toComponent())
                    return
                }

                val targetPlayer = Storage.onlinePlayers[melodyPlayer.callPendingTarget?.uuid] ?: return
                if (!targetPlayer.isActiveVoice || targetPlayer.isInCall || !targetPlayer.isCallPending || targetPlayer.callToggle) {
                    player.sendMessage("<prefix><count_color>${targetPlayer.name} <text>is not Available Please try Again later.".toComponent())
                    return
                }

                MelodyManager.denyCall(melodyPlayer, targetPlayer)

                coolDown[player.uniqueId] = System.currentTimeMillis()
                return
            }

            "toggle" -> {
                if (!player.hasPermission("melodymine.call.toggle")) {
                    player.sendMessage("<prefix>You dont have permission to use this command.".toComponent())
                    return
                }

                val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return

                MelodyManager.toggleCall(melodyPlayer)
                coolDown[player.uniqueId] = System.currentTimeMillis()
                return
            }

            else -> sendCallHelpMessage(player)
        }

    }

    private fun sendCallHelpMessage(player: Player) {
        player.sendMessage(Storage.contentHeader.toComponent())
        player.sendMessage("")
        player.sendMessage("<click:run_command:'${syntax} start'><hover:show_text:'<text_hover>Click to run <i>${syntax} start</i>'><text_hover>${syntax} start <player> <#FFF4E4><bold>|</bold> <text>Start Call to Someone.</hover></click>".toComponent())
        player.sendMessage("<click:run_command:'${syntax} end'><hover:show_text:'<text_hover>Click to run <i>${syntax} end</i>'><text_hover>${syntax} end <#FFF4E4><bold>|</bold> <text>End Call You're in.</hover></click>".toComponent())
        player.sendMessage("<click:run_command:'${syntax} accept'><hover:show_text:'<text_hover>Click to run <i>${syntax} accept</i>'><text_hover>${syntax} accept <#FFF4E4><bold>|</bold> <text>Accept Call Request.</hover></click>".toComponent())
        player.sendMessage("<click:run_command:'${syntax} deny'><hover:show_text:'<text_hover>Click to run <i>${syntax} deny</i>'><text_hover>${syntax} deny <#FFF4E4><bold>|</bold> <text>Deny Call Request.</hover></click>".toComponent())
        player.sendMessage("<click:run_command:'${syntax} toggle'><hover:show_text:'<text_hover>Click to run <i>${syntax} toggle</i>'><text_hover>${syntax} toggle <#FFF4E4><bold>|</bold> <text>Toggle Call Requests.</hover></click>".toComponent())
        player.sendMessage("")
        player.sendMessage(Storage.contentFooter.toComponent())
    }
}
