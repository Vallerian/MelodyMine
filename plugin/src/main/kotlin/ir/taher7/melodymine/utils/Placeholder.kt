package ir.taher7.melodymine.utils

import ir.taher7.melodymine.MelodyMine
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
                if (melodyPlayer.webIsOnline) return Storage.placeholderWebOnlineTrue
                return Storage.placeholderWebOnlineFalse
            }

            "voice" -> {
                if (melodyPlayer.isActiveVoice) return Storage.placeholderVoiceActiveTrue
                return Storage.placeholderVoiceActiveFalse
            }

            "mute" -> {
                if (melodyPlayer.isMute) return Storage.placeholderMuteTrue
                return Storage.placeholderMuteFalse
            }

            "adminmode" -> {
                if (melodyPlayer.isMute) return Storage.placeholderAdminModeTrue
                return Storage.placeholderAdminModeFalse
            }

            "status" -> {
                if (melodyPlayer.isMute) return Storage.placeholderMuteTrue
                if (melodyPlayer.isActiveVoice) return Storage.placeholderVoiceActiveTrue
                return Storage.placeholderVoiceActiveFalse
            }

            else -> {}
        }
        return null
    }

}