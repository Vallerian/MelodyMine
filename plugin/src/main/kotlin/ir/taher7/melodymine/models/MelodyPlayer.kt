package ir.taher7.melodymine.models

import com.google.gson.annotations.Expose
import ir.taher7.melodymine.core.TalkBossBar
import ir.taher7.melodymine.core.TalkNameTag
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

data class MelodyPlayer(
    @Expose(deserialize = false) var player: Player? = null,
    @Expose var id: Int,
    @Expose val uuid: String,
    @Expose val name: String,
    @Expose var server: String,
    @Expose var socketID: String? = null,
    @Expose var verifyCode: String? = null,
    @Expose var isActiveVoice: Boolean = false,
    @Expose var serverIsOnline: Boolean = false,
    @Expose var webIsOnline: Boolean = false,
    @Expose var adminMode: Boolean = false,
    @Expose var isMute: Boolean = false,
    @Expose var isToggle: Boolean = false,
    @Expose var isSelfMute: Boolean = true,
    @Expose var isDeafen: Boolean = true,
    @Expose var isInCall: Boolean = false,
    @Expose var callTarget: MelodyPlayer? = null,
    @Expose var isCallPending: Boolean = false,
    @Expose var callPendingTarget: MelodyPlayer? = null,
    @Expose var callToggle: Boolean = false,
    @Expose var isStartCall: Boolean = false,
    @Expose var pendingTask: BukkitTask? = null,
    @Expose var isTalk: Boolean = false,
    @Expose(deserialize = false) var talkBossBar: TalkBossBar? = null,
    @Expose(deserialize = false) var talkNameTag: TalkNameTag? = null,
    @Expose(deserialize = false) var isSendOffer: ArrayList<String> = arrayListOf(),
) {
    fun updateWebData(player: MelodyPlayer) {
        socketID = player.socketID
        isActiveVoice = player.isActiveVoice
        verifyCode = player.verifyCode
        webIsOnline = player.webIsOnline
    }

    fun updateControl(control: MelodyControl) {
        when (control.type) {
            "mic" -> {
                isSelfMute = control.value
                if (!control.value) {
                    talkBossBar?.setBossBarSelfMute()
                    talkNameTag?.setNameTagSelfMute()

                } else {
                    talkBossBar?.setBossBarInactive()
                    talkNameTag?.setNameTagInactive()
                }
            }

            "sound" -> {
                isDeafen = control.value
            }
        }
    }
}
