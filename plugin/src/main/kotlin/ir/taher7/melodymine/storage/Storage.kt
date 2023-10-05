package ir.taher7.melodymine.storage

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.models.MelodyPlayer
import java.util.*
import kotlin.collections.ArrayList


object Storage {
    // config yml data
    lateinit var server: String
    lateinit var website: String
    lateinit var websocket: String
    var hearDistance: Int = 20
    var hearLazy: Boolean = true
    var forceVoice: Boolean = false
    var updateDistanceTime: Long = 10L
    var websocketKey: String = ""

    // messages
    lateinit var prefix: String
    lateinit var websiteMessage: String

    // logger messages
    lateinit var websiteJoinLogger: String
    lateinit var websiteLeaveLogger: String
    lateinit var websiteStartVoiceLogger: String
    lateinit var websiteEndVoiceLogger: String

    // command messages
    lateinit var reloadDescription: String
    lateinit var adminmodeDescription: String
    lateinit var startDescription: String
    lateinit var muteDescription: String
    lateinit var unmuteDescription: String

    // placeholder messages
    lateinit var placeholderWebOnlineTrue: String
    lateinit var placeholderWebOnlineFalse: String
    lateinit var placeholderVoiceActiveTrue: String
    lateinit var placeholderAdminModeTrue: String
    lateinit var placeholderAdminModeFalse: String
    lateinit var placeholderVoiceActiveFalse: String
    lateinit var placeholderMuteTrue: String
    lateinit var placeholderMuteFalse: String

    // website status
    lateinit var joinWebsiteMessage: String
    lateinit var leaveWebsiteMessage: String
    lateinit var startVoiceMessage: String
    lateinit var endVoiceMessage: String

    lateinit var joinWebsiteType: String
    lateinit var leaveWebsiteType: String
    lateinit var startVoiceType: String
    lateinit var leaveEndType: String

    // plugin data
    val onlinePlayers = hashMapOf<String, MelodyPlayer>()
    val subCommands = ArrayList<SubCommand>()
    val muteCoolDown = hashMapOf<UUID, Long>()

    init {
        reload()
    }

    fun reload() {
        MelodyMine.instance.reloadConfig()
        val config = MelodyMine.instance.config
        server = config.getString("server") ?: ""
        website = config.getString("website") ?: ""
        websocket = config.getString("websocket-url") ?: ""
        websocketKey = config.getString("websocket-auth-key") ?: ""

        hearDistance = config.getInt("hear-distance")
        updateDistanceTime = config.getLong("update-distance-time")
        hearLazy = config.getBoolean("hear-lazy")
        forceVoice = config.getBoolean("force-voice")

        prefix = config.getString("prefix") ?: ""
        websiteMessage = config.getString("website-message") ?: ""

        websiteJoinLogger = config.getString("website-join-logger") ?: ""
        websiteLeaveLogger = config.getString("website-leave-logger") ?: ""
        websiteStartVoiceLogger = config.getString("website-start-voice-logger") ?: ""
        websiteEndVoiceLogger = config.getString("website-end-voice-logger") ?: ""

        reloadDescription = config.getString("reload-description") ?: ""
        adminmodeDescription = config.getString("adminmode-description") ?: ""
        startDescription = config.getString("start-description") ?: ""
        muteDescription = config.getString("mute-description") ?: ""
        unmuteDescription = config.getString("unmute-description") ?: ""

        placeholderWebOnlineTrue = config.getString("placeholder-web-online-true") ?: ""
        placeholderWebOnlineFalse = config.getString("placeholder-web-online-false") ?: ""
        placeholderVoiceActiveTrue = config.getString("placeholder-voice-active-true") ?: ""
        placeholderAdminModeTrue = config.getString("placeholder-adminmode-true") ?: ""
        placeholderAdminModeFalse = config.getString("placeholder-adminmode-false") ?: ""
        placeholderVoiceActiveFalse = config.getString("placeholder-voice-active-false") ?: ""
        placeholderMuteTrue = config.getString("placeholder-mute-true") ?: ""
        placeholderMuteFalse = config.getString("placeholder-mute-false") ?: ""

        joinWebsiteMessage = config.getString("join-website-message") ?: ""
        leaveWebsiteMessage = config.getString("leave-website-message") ?: ""
        startVoiceMessage = config.getString("start-voice-message") ?: ""
        endVoiceMessage = config.getString("end-voice-message") ?: ""

        joinWebsiteType = config.getString("join-website-message-type") ?: ""
        leaveWebsiteType = config.getString("leave-website-message-type") ?: ""
        startVoiceType = config.getString("start-voice-message-type") ?: ""
        leaveEndType = config.getString("end-voice-message-type") ?: ""

    }


}