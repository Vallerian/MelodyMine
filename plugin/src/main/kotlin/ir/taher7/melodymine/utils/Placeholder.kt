package ir.taher7.melodymine.utils

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Storage
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class Placeholder : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "melodymine"
    }

    override fun getAuthor(): String {
        return "TAHER7"
    }

    override fun getVersion(): String {
        return MelodyMine.instance.description.version
    }

    override fun canRegister(): Boolean {
        return true
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        player ?: return null
        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return null

        when (params) {
            "server" -> {
                return melodyPlayer.server
            }

            "web" -> {
                if (melodyPlayer.webIsOnline) return Messages.getMessageString("placeholder.web_online_true")
                return Messages.getMessageString("placeholder.web_online_false")
            }

            "voice" -> {
                if (melodyPlayer.isActiveVoice) return Messages.getMessageString("placeholder.voice_active_true")
                return Messages.getMessageString("placeholder.voice_active_false")
            }

            "mute" -> {
                if (melodyPlayer.isMute) return Messages.getMessageString("placeholder.server_mute_true")
                return Messages.getMessageString("placeholder.server_mute_false")
            }

            "adminmode" -> {
                if (melodyPlayer.isMute) return Messages.getMessageString("placeholder.adminmode_true")
                return Messages.getMessageString("placeholder.adminmode_false")
            }

            "status" -> {
                if (melodyPlayer.isMute) return Messages.getMessageString("placeholder.server_mute_true")
                if (melodyPlayer.isActiveVoice) return Messages.getMessageString("placeholder.voice_active_true")
                return Messages.getMessageString("placeholder.voice_active_false")
            }


            "self_mute" -> {
                if (!melodyPlayer.isActiveVoice || !melodyPlayer.isSelfMute) return Messages.getMessageString("placeholder.self_mute")
                return Messages.getMessageString("placeholder.self_unmute")
            }

            "deafen" -> {
                if (melodyPlayer.isDeafen) return Messages.getMessageString("placeholder.self_deafen")
                return Messages.getMessageString("placeholder.self_undeafen")
            }

            "control" -> {
                if (!melodyPlayer.isActiveVoice) return "${Messages.getMessageString("placeholder.self_deafen")}${Messages.getMessageString("placeholder.self_mute")}"
                if (!melodyPlayer.isSelfMute && !melodyPlayer.isDeafen) return "${Messages.getMessageString("placeholder.self_deafen")} ${Messages.getMessageString("placeholder.self_mute")}"
                if (!melodyPlayer.isDeafen) return Messages.getMessageString("placeholder.self_deafen")
                if (!melodyPlayer.isSelfMute) return Messages.getMessageString("placeholder.self_mute")
                return "${Messages.getMessageString("placeholder.self_undeafen")}${Messages.getMessageString("placeholder.self_unmute")}"

            }

            "call" -> {
                if (!melodyPlayer.isInCall) return ""
                return melodyPlayer.callTarget?.name ?: ""
            }

            "pending_call" -> {
                if (!melodyPlayer.isCallPending) return ""
                return melodyPlayer.callPendingTarget?.name ?: ""
            }

            else -> {}
        }
        return null
    }

}