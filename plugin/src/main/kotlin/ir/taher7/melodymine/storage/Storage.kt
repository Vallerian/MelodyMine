package ir.taher7.melodymine.storage

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.models.*
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

    var sound3D: Boolean = true
    var lazyHear: Boolean = true
    var maxDistance: Int = 30
    var refDistance: Int = 5
    var innerAngle: Int = 120
    var outerAngle: Int = 180
    var outerVolume: Double = 0.3
    var forceVoice: Boolean = false
    var websocketKey: String = ""

    var disableWorld: MutableList<String> = mutableListOf()

    // renew configs
    var updateDistanceInterval: Long = 10L
    var updateConnectionDistance: Int = 30
    var updateVolumeDistance: Int = 80
    var updateDisconnectDistance: Int = 230

    // messages
    lateinit var prefix: String
    lateinit var text: String
    lateinit var textHover: String
    lateinit var count_color: String
    lateinit var websiteMessage: String
    lateinit var contentHeader: String
    lateinit var contentFooter: String

    // logger messages
    lateinit var websiteJoinLogger: String
    lateinit var websiteLeaveLogger: String
    lateinit var websiteStartVoiceLogger: String
    lateinit var websiteEndVoiceLogger: String

    // command messages
    lateinit var reloadDescription: String
    lateinit var adminmodeDescription: String
    lateinit var startDescription: String
    lateinit var statusDescription: String
    lateinit var muteDescription: String
    lateinit var unmuteDescription: String
    lateinit var toggleDescription: String
    lateinit var controlDescription: String
    lateinit var callDescription: String

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

    // call configs
    var callPendingTime: Long = 200

    // plugin data
    val onlinePlayers = hashMapOf<String, MelodyPlayer>()
    val subCommands = ArrayList<SubCommand>()
    val muteCoolDown = hashMapOf<UUID, Long>()
    val playerMuteShortcut = ArrayList<UUID>()

    // talk view data
    var isEnableBossBar: Boolean = true
    var isEnableNameTag: Boolean = true

    val bossbarConfigs = hashMapOf<String, BossBarConfig>()
    val nameTagConfigs = hashMapOf<String, NameTagConfig>()


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

        sound3D = config.getBoolean("3D-sound")
        lazyHear = config.getBoolean("hear-lazy")
        maxDistance = config.getInt("max-distance")
        refDistance = config.getInt("ref-distance")
        innerAngle = config.getInt("inner-angle")
        outerAngle = config.getInt("outer-angle")
        outerVolume = config.getDouble("outer-volume")

        forceVoice = config.getBoolean("force-voice")

        prefix = config.getString("prefix") ?: ""
        text = config.getString("text") ?: ""
        textHover = config.getString("text-hover") ?: ""
        count_color = config.getString("count-color") ?: ""
        contentHeader = config.getString("content-header") ?: ""
        contentFooter = config.getString("content-footer") ?: ""

        websiteMessage = config.getString("website-message") ?: ""

        websiteJoinLogger = config.getString("website-join-logger") ?: ""
        websiteLeaveLogger = config.getString("website-leave-logger") ?: ""
        websiteStartVoiceLogger = config.getString("website-start-voice-logger") ?: ""
        websiteEndVoiceLogger = config.getString("website-end-voice-logger") ?: ""

        reloadDescription = config.getString("reload-description") ?: ""
        adminmodeDescription = config.getString("adminmode-description") ?: ""
        startDescription = config.getString("start-description") ?: ""
        statusDescription = config.getString("status-description") ?: ""
        muteDescription = config.getString("mute-description") ?: ""
        unmuteDescription = config.getString("unmute-description") ?: ""
        toggleDescription = config.getString("toggle-description") ?: ""
        controlDescription = config.getString("control-description") ?: ""
        callDescription = config.getString("call-description") ?: ""

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

        forceVoiceTitle = config.getBoolean("force-voice-title")
        forceVoiceTitleMessage = config.getString("force-voice-title-message") ?: ""
        forceVoiceSubtitleMessage = config.getString("force-voice-subtitle-message") ?: ""

        muteToggleShortcut = config.getBoolean("mute-toggle-shortcut")

        muteToggleMessage = config.getString("mute-toggle-message") ?: ""
        unMuteToggleMessage = config.getString("unmute-toggle-message") ?: ""
        deafenToggleMessage = config.getString("deafen-toggle-message") ?: ""
        unDeafenToggleMessage = config.getString("un-deafen-toggle-message") ?: ""

        callPendingTime = config.getLong("call-pending-time")

        val bossBarSection = MelodyMine.instance.config.getConfigurationSection("bossbar-talk") ?: return
        val nameTagSection = MelodyMine.instance.config.getConfigurationSection("nametag-talk") ?: return
        isEnableBossBar = bossBarSection.getBoolean("enable")
        isEnableNameTag = nameTagSection.getBoolean("enable")

        val bossBarConfigSection = bossBarSection.getConfigurationSection("configs") ?: return
        val nameTagConfigSection = nameTagSection.getConfigurationSection("configs") ?: return

        for (key in bossBarConfigSection.getKeys(false)) {
            val config = bossBarConfigSection.getConfigurationSection(key) ?: continue
            bossbarConfigs[key] = BossBarConfig(
                enable = config.getBoolean("enable"),
                color = config.getString("color") ?: "white",
                text = config.getString("text") ?: ""
            )
        }

        for (key in nameTagConfigSection.getKeys(false)) {
            val config = nameTagConfigSection.getConfigurationSection(key) ?: continue
            nameTagConfigs[key] = NameTagConfig(
                enable = config.getBoolean("enable"),
                textVisible = config.getBoolean("text-visible"),
                text = config.getString("text") ?: "",
                position = Position(
                    x = config.getConfigurationSection("position")?.getDouble("x") ?: 0.0,
                    y = config.getConfigurationSection("position")?.getDouble("y") ?: 2.1,
                    z = config.getConfigurationSection("position")?.getDouble("z") ?: 0.0,
                ),
                item = Item(
                    type = config.getConfigurationSection("item")?.getString("type") ?: "AIR",
                    customData = config.getConfigurationSection("item")?.getInt("custom-data") ?: 0,
                )
            )
        }


        val renewSection = MelodyMine.instance.config.getConfigurationSection("renew-configs") ?: return
        updateDistanceInterval = renewSection.getLong("update-interval")
        updateConnectionDistance = renewSection.getInt("connect-distance")
        updateVolumeDistance = renewSection.getInt("volume-distance")
        updateDisconnectDistance = renewSection.getInt("disconnect-distance")


        disableWorld = config.getStringList("disable-worlds")

    }


}