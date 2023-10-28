package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.AdventureUtils.sendMessage
import ir.taher7.melodymine.utils.AdventureUtils.toComponent
import org.bukkit.entity.Player
import java.util.*

class Control : SubCommand() {
    private val coolDown = hashMapOf<UUID, Long>()

    override var name = "control"
    override var description = "Mute / Deafen Yourself in website."
    override var syntax = "/melodymine control <mute | deafen>"
    override var permission = "melodymine.control"
    override fun handler(player: Player, args: Array<out String>) {
        if (coolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - coolDown[player.uniqueId]!!) <= 5000) {
            player.sendMessage("<prefix>You can use this command after <#DDB216>${((5000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)}</#DDB216> second.".toComponent())
            return
        }

        if (args.size != 2) {
            sendControlHelpMessage(player)
            return
        }

        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
        if (!melodyPlayer.isActiveVoice) {
            player.sendMessage("<prefix>You must active your voice.".toComponent())
            return
        }

        if (args[1].equals("mute", true)) {
            MelodyManager.setPlayerSelfMute(melodyPlayer, !melodyPlayer.isSelfMute)
            if (melodyPlayer.isSelfMute) {
                player.sendMessage(Storage.unMuteToggleMessage.toComponent())
            } else {
                player.sendMessage(Storage.muteToggleMessage.toComponent())
            }
            coolDown[player.uniqueId] = System.currentTimeMillis()
            return
        }

        if (args[1].equals("deafen", true)) {
            MelodyManager.setPlayerDeafen(melodyPlayer, !melodyPlayer.isDeafen)
            if (melodyPlayer.isDeafen) {
                player.sendMessage(Storage.unDeafenToggleMessage.toComponent())
            } else {
                player.sendMessage(Storage.deafenToggleMessage.toComponent())
            }
            coolDown[player.uniqueId] = System.currentTimeMillis()
            return
        }

        sendControlHelpMessage(player)
    }

    private fun sendControlHelpMessage(player: Player) {
        player.sendMessage("<click:run_command:'/melodymine control mute'><hover:show_text:'<hover_text>Click to run this command <i>/melodymine control mute</i>'><prefix>Use: <i>/melodymine control mute</i></hover></click>".toComponent())
        player.sendMessage("<click:run_command:'/melodymine control deafen'><hover:show_text:'<hover_text>Click to run this command <i>/melodymine control deafen</i>'><prefix>Use: <i>/melodymine control deafen</i></hover></click>".toComponent())
    }
}