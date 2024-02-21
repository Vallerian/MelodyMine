package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class Status : SubCommand() {
    private val coolDown = hashMapOf<UUID, Long>()

    override var name = "status"
    override var description = Storage.statusDescription
    override var syntax = "/melodymine status"
    override var permission = "melodymine.status"

    override fun handler(player: Player, args: Array<out String>) {
        if (coolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - coolDown[player.uniqueId]!!) <= 5000) {
            player.sendMessage("<prefix>You can use this command after <count_color>${((5000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)}<text> second.".toComponent())
            return
        }
        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return


        if (args.size == 1) {

            if (!melodyPlayer.isActiveVoice) {
                player.sendMessage("<click:run_command:'/melodymine start link'><hover:show_text:'<text_hover>Click to run <i>/melodymine start link</i>'><prefix>You Must First Connect to Voice Chat <text_hover>/melodymine start link <text>.</hover></click>".toComponent())
                return
            }

            player.sendMessage(Storage.contentHeader.toComponent())
            player.sendMessage("")
            player.sendMessage("<prefix><count_color>${melodyPlayer.name} <text>Voice Status:".toComponent())
            player.sendMessage("<prefix>Activate: <count_color>Online<text>.".toComponent())
            player.sendMessage("<prefix>Microphone: <count_color>${if (!melodyPlayer.isSelfMute) "Mute" else "Active"}<text>.".toComponent())
            player.sendMessage("<prefix>Speaker: <count_color>${if (!melodyPlayer.isDeafen) "Deafen" else "Active"}<text>.".toComponent())
            player.sendMessage("<prefix>Call: <text_hover>${if (melodyPlayer.isInCall) "You're In Call With <count_color>${melodyPlayer.callTarget?.name}" else "<count_color>OFF"}<text>.".toComponent())
            player.sendMessage(
                "<prefix>Voice Connections: <count_color>${melodyPlayer.isSendOffer.size}<text>, Players: <count_color>${
                    melodyPlayer.isSendOffer.map { uuid -> Storage.onlinePlayers[uuid]?.name }
                        .joinToString("<text>, <count_color>")
                }<text>.".toComponent()
            )
            player.sendMessage("")
            player.sendMessage(Storage.contentFooter.toComponent())

            coolDown[player.uniqueId] = System.currentTimeMillis()
            return
        }

        if (args.size == 2) {

            if (!player.hasPermission("melodymine.status.others")) {
                player.sendMessage("<prefix>You don't have permission to use this command.".toComponent())
                return
            }

            val targetMelodyPlayer = Bukkit.getPlayer(args[1])
            if (targetMelodyPlayer == null) {
                player.sendMessage("<prefix><count_color>${args[1]} <text>Not Found.".toComponent())
                return
            }

            val melodyTargetPlayer = Storage.onlinePlayers[targetMelodyPlayer.uniqueId.toString()]
            if (melodyTargetPlayer == null) {
                player.sendMessage("<prefix><count_color>${args[1]} <text>Not Found.".toComponent())
                return
            }

            if (!melodyTargetPlayer.isActiveVoice) {
                player.sendMessage("<prefix><count_color>${melodyTargetPlayer.name} <text>is not Active in Voice Chat.".toComponent())
                return
            }

            player.sendMessage(Storage.contentHeader.toComponent())
            player.sendMessage("")
            player.sendMessage("<prefix><count_color>${melodyTargetPlayer.name} <text>Voice Status:".toComponent())
            player.sendMessage("<prefix>Activate: <count_color>Online<text>.".toComponent())
            player.sendMessage("<prefix>Microphone: <count_color>${if (!melodyTargetPlayer.isSelfMute) "Mute" else "Active"}<text>.".toComponent())
            player.sendMessage("<prefix>Speaker: <count_color>${if (!melodyTargetPlayer.isDeafen) "Deafen" else "Active"}<text>.".toComponent())
            player.sendMessage("<prefix>Call: <text_hover>${if (melodyTargetPlayer.isInCall) "You're In Call With <count_color>${melodyTargetPlayer.callTarget?.name}" else "<count_color>OFF"}<text>.".toComponent())
            player.sendMessage(
                "<prefix>Voice Connections: <count_color>${melodyTargetPlayer.isSendOffer.size}<text>, Players: <count_color>${
                    melodyTargetPlayer.isSendOffer.map { uuid -> Storage.onlinePlayers[uuid]?.name }
                        .joinToString("<text>, <count_color>")
                }<text>.".toComponent()
            )
            player.sendMessage("")
            player.sendMessage(Storage.contentFooter.toComponent())
            coolDown[player.uniqueId] = System.currentTimeMillis()
            return
        }

        sendStatusHelpMessage(player)
        coolDown[player.uniqueId] = System.currentTimeMillis()
    }

    private fun sendStatusHelpMessage(player: Player) {
        player.sendMessage(Storage.contentHeader.toComponent())
        player.sendMessage("")
        player.sendMessage("<click:run_command:'${syntax} <player>'><hover:show_text:'<text_hover>Click to run <i>${syntax} player</i>'><text_hover>${syntax} <#FFF4E4><bold>|</bold> <text>Check Your Status.</hover></click>".toComponent())
        if (player.hasPermission("melodymine.status.others")) {
            player.sendMessage("<click:run_command:'${syntax}'><hover:show_text:'<text_hover>Click to run <i>${syntax}</i>'><text_hover>${syntax} <player> <#FFF4E4><bold>|</bold> <text>Check Other Players Voice Status.</hover></click>".toComponent())
        }
        player.sendMessage("")
        player.sendMessage(Storage.contentFooter.toComponent())
    }
}