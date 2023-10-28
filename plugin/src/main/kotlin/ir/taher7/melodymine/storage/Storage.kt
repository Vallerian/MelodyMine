package ir.taher7.melodymine.storage

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.models.MelodyPlayer
import java.util.*
import kotlin.collections.ArrayList


object Storage {
    // config yml data
    lateinit var host: String
    lateinit var port: String
    lateinit var user: String
    lateinit var password: String
    lateinit var dbName: String

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
    lateinit var placeholderSelfMute: String
    lateinit var placeholderSelfUnMute: String
    lateinit var placeholderDeafen: String
    lateinit var placeholderUnDeafen: String

    // website status
    lateinit var joinWebsiteMessage: String
    lateinit var leaveWebsiteMessage: String
    lateinit var startVoiceMessage: String
    lateinit var endVoiceMessage: String

    lateinit var joinWebsiteType: String
    lateinit var leaveWebsiteType: String
    lateinit var startVoiceType: String
    lateinit var leaveEndType: String

    // force voice
    lateinit var forceVoiceMessage: String
    var forceVoiceTitle: Boolean = true
    lateinit var forceVoiceTitleMessage: String
    lateinit var forceVoiceSubtitleMessage: String

    // shortcut
    var muteToggleShortcut: Boolean = true

    // control messages
    lateinit var muteToggleMessage: String
    lateinit var unMuteToggleMessage: String
    lateinit var deafenToggleMessage: String
    lateinit var unDeafenToggleMessage: String

    // plugin data
    val onlinePlayers = hashMapOf<String, MelodyPlayer>()
    val subCommands = ArrayList<SubCommand>()
    val muteCoolDown = hashMapOf<UUID, Long>()
    val playerMuteShortcut = ArrayList<UUID>()


    init {
        reload()
    }

    fun reload() {
        MelodyMine.instance.reloadConfig()
        val config = MelodyMine.instance.config
        val database = MelodyMine.instance.config.getConfigurationSection("database")
        if (database == null) {
            MelodyMine.instance.logger.severe("Database Config not found, Plugin disabled.")
            return
        }
        host = database.getString("host").toString()
        port = database.getString("port").toString()
        user = database.getString("user").toString()
        password = database.getString("password").toString()
        dbName = database.getString("database_name").toString()
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
        placeholderSelfMute = config.getString("placeholder-self-mute") ?: ""
        placeholderSelfUnMute = config.getString("placeholder-self-unmute") ?: ""
        placeholderDeafen = config.getString("placeholder-self-deafen") ?: ""
        placeholderUnDeafen = config.getString("placeholder-self-undeafen") ?: ""

        joinWebsiteMessage = config.getString("join-website-message") ?: ""
        leaveWebsiteMessage = config.getString("leave-website-message") ?: ""
        startVoiceMessage = config.getString("start-voice-message") ?: ""
        endVoiceMessage = config.getString("end-voice-message") ?: ""

        joinWebsiteType = config.getString("join-website-message-type") ?: ""
        leaveWebsiteType = config.getString("leave-website-message-type") ?: ""
        startVoiceType = config.getString("start-voice-message-type") ?: ""
        leaveEndType = config.getString("end-voice-message-type") ?: ""

        forceVoiceMessage = config.getString("force-voice-message") ?: ""
        forceVoiceTitle = config.getBoolean("force-voice-title")
        forceVoiceTitleMessage = config.getString("force-voice-title-message") ?: ""
        forceVoiceSubtitleMessage = config.getString("force-voice-subtitle-message") ?: ""

        muteToggleShortcut = config.getBoolean("mute-toggle-shortcut")

        muteToggleMessage = config.getString("mute-toggle-message") ?: ""
        unMuteToggleMessage = config.getString("unmute-toggle-message") ?: ""
        deafenToggleMessage = config.getString("deafen-toggle-message") ?: ""
        unDeafenToggleMessage = config.getString("un-deafen-toggle-message") ?: ""

    }


}