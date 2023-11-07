package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.entity.Player
import java.util.*

class Control : SubCommand() {
    private val coolDown = hashMapOf<UUID, Long>()

    override var name = "control"
    override var description = Storage.controlDescription
    override var syntax = "/melodymine control"
    override var permission = "melodymine.control"
    override fun handler(player: Player, args: Array<out String>) {
        if (coolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - coolDown[player.uniqueId]!!) <= 5000) {
            player.sendMessage("<prefix>You can use this command after <count_color>${((5000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)}<text> second.".toComponent())
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
        player.sendMessage(Storage.contentHeader.toComponent())
        player.sendMessage("")
        player.sendMessage("<click:run_command:'${syntax} mute'><hover:show_text:'<text_hover>Click to run <i>${syntax} mute</i>'><text_hover>${syntax} mute <#FFF4E4><bold>|</bold> <text>Mute Yourself in Website.</hover></click>".toComponent())
        player.sendMessage("<click:run_command:'${syntax} deafen'><hover:show_text:'<text_hover>Click to run <i>${syntax} deafen</i>'><text_hover>${syntax} deafen <#FFF4E4><bold>|</bold> <text>Deafen Yourself in Website.</hover></click>".toComponent())
        player.sendMessage("")
        player.sendMessage(Storage.contentFooter.toComponent())
    }
}