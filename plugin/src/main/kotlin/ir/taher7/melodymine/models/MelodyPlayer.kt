package ir.taher7.melodymine.models

import com.google.gson.annotations.Expose
import org.bukkit.entity.Player

data class MelodyPlayer(
    @Expose(deserialize = false) var player: Player? = null,
    @Expose val uuid: String,
    @Expose val name: String,
    @Expose var server: String,
    @Expose var socketID: String? = null,
    @Expose var verifyCode: String? = null,
    @Expose var serverIp: String? = null,
    @Expose var webIp: String? = null,
    @Expose var isActiveVoice: Boolean = false,
    @Expose var serverIsOnline: Boolean = false,
    @Expose var webIsOnline: Boolean = false,
    @Expose var adminMode: Boolean = false,
    @Expose var isMute: Boolean = false,
    @Expose var isToggle: Boolean = false,
    @Expose(deserialize = false) var isSendOffer: ArrayList<String> = arrayListOf(),
) {

    fun updateWebData(player: MelodyPlayer) {
        socketID = player.socketID
        isActiveVoice = player.isActiveVoice
        verifyCode = player.verifyCode
        webIp = player.webIp
        webIsOnline = player.webIsOnline
    }

}
