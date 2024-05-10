package ir.taher7.melodymine.storage

import ir.taher7.melodymine.MelodyMine

object Settings {

    lateinit var host: String
    lateinit var port: String
    lateinit var username: String
    lateinit var password: String
    lateinit var database: String

    lateinit var language: String

    lateinit var server: String
    lateinit var domain: String
    lateinit var clientPort: String
    lateinit var serverPort: String
    lateinit var pluginKey: String
    var autoStart: Boolean = true

    var forceVoice: Boolean = false
    var forceVoiceTitle: Boolean = true
    var forceVoiceInterval: Long = 300
    var forceVoiceDamage: Boolean = false

    var shortcut: Boolean = true
    var shortcutCoolDown: Int = 1000

    var callPendingTime: Long = 600

    var lazyHear: Boolean = true
    var maxDistance: Int = 15
    var refDistance: Int = 5
    var rolloffFactor: Int = 1
    var innerAngle: Int = 120
    var outerAngle: Int = 180
    var outerVolume: Double = 0.3

    var renewInterval: Long = 30L
    var renewConnection: Int = 15
    var renewVolume: Int = 30
    var renewDisconnect: Int = 80

    var disableWorlds: MutableList<String> = mutableListOf()

    var statusJoinWeb: String = "message"
    var statusLeaveWeb: String = "message"
    var statusStartVoice: String = "message"
    var statusEndVoice: String = "message"

    var commandsCoolDown: Int = 3000

    fun load() {
        val config = MelodyMine.instance.settingsConfig.config

        val databaseConfig = MelodyMine.instance.databaseConfig.config
        val mySQL = databaseConfig.getConfigurationSection("mysql_configs") ?: return
        host = mySQL.getString("host") ?: ""
        port = mySQL.getString("port") ?: ""
        username = mySQL.getString("username") ?: ""
        password = mySQL.getString("password") ?: ""
        database = mySQL.getString("database") ?: ""


        language = config.getString("language") ?: "en_US"

        server = config.getString("initial_configs.server") ?: "Lobby"
        domain = config.getString("initial_configs.domain") ?: "localhost"
        clientPort = config.getString("initial_configs.client_port") ?: "3000"
        serverPort = config.getString("initial_configs.server_port") ?: "4000"
        pluginKey = config.getString("initial_configs.plugin_key") ?: ""

        autoStart = config.getBoolean("auto_start")

        forceVoice = config.getBoolean("force_voice.enable")
        forceVoiceTitle = config.getBoolean("force_voice.send_title")
        forceVoiceInterval = config.getLong("force_voice.send_interval")
        forceVoiceDamage = config.getBoolean("force_voice.damage")

        shortcut = config.getBoolean("mute_toggle_shortcut.enable")
        shortcutCoolDown = config.getInt("mute_toggle_shortcut.cool_down")

        callPendingTime = config.getLong("call_pending_time")

        lazyHear = config.getBoolean("sound_configs.hear_lazy")
        maxDistance = config.getInt("sound_configs.max_distance")
        refDistance = config.getInt("sound_configs.ref_distance")
        rolloffFactor = config.getInt("sound_configs.rolloff_factor")
        innerAngle = config.getInt("sound_configs.inner_angle")
        outerAngle = config.getInt("sound_configs.outer_angle")
        outerVolume = config.getDouble("sound_configs.outer_volume")

        renewInterval = config.getLong("renew_configs.interval")
        renewConnection = config.getInt("renew_configs.connect_distance")
        renewVolume = config.getInt("renew_configs.volume_distance")
        renewDisconnect = config.getInt("renew_configs.disconnect_distance")

        disableWorlds = config.getStringList("disable_worlds")

        statusJoinWeb = config.getString("show_status_type.join_website") ?: "message"
        statusLeaveWeb = config.getString("show_status_type.leave_website") ?: "message"
        statusStartVoice = config.getString("show_status_type.start_voice") ?: "message"
        statusEndVoice = config.getString("show_status_type.end_voice") ?: "message"

        commandsCoolDown = config.getInt("commands_cool_down")


    }
}