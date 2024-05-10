package ir.taher7.melodymine

import ch.qos.logback.classic.Level
import com.jeff_media.updatechecker.UpdateCheckSource
import com.jeff_media.updatechecker.UpdateChecker
import ir.taher7.melodymine.commands.CommandManager
import ir.taher7.melodymine.commands.TabCompletionManager
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.listeners.*
import ir.taher7.melodymine.services.Websocket
import ir.taher7.melodymine.services.WebsocketRenewData
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Settings
import ir.taher7.melodymine.storage.Talk
import ir.taher7.melodymine.storage.YamlConfig
import ir.taher7.melodymine.utils.Placeholder
import ir.taher7.melodymine.utils.Utils
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.slf4j.LoggerFactory

class MelodyMine : JavaPlugin() {

    lateinit var settingsConfig: YamlConfig
    lateinit var databaseConfig: YamlConfig
    lateinit var talkConfig: YamlConfig
    val languages = hashMapOf<String, YamlConfig>()
    private val shortLang = listOf(
        "en_US",
        "fa_IR"
    )


    override fun onEnable() {
        instance = this
        loadConfig()
        Utils.sendMelodyFiglet()
        val logger = LoggerFactory.getLogger("com.zaxxer.hikari") as ch.qos.logback.classic.Logger
        logger.setLevel(Level.ERROR)


        Database.resetDate()
        Websocket.connect()
        WebsocketRenewData()
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) Placeholder().register()

        server.pluginManager.registerEvents(MelodyMineListener(), this)
        server.pluginManager.registerEvents(QRCodeListener(), this)
        server.pluginManager.registerEvents(ShortcutListener(), this)
        server.pluginManager.registerEvents(CallListener(), this)
        server.pluginManager.registerEvents(NameTagListener(), this)


        getCommand("melodymine")?.setExecutor(CommandManager())
        getCommand("melodymine")?.tabCompleter = TabCompletionManager()


        Metrics(this, 20020)
        checkUpdate()

    }


    override fun onDisable() {
        Database.resetDate()
        Database.hikari.close()
    }


    private fun loadConfig() {
        dataFolder.mkdir()

        settingsConfig = YamlConfig(dataFolder, "settings.yml")
        databaseConfig = YamlConfig(dataFolder, "mysql.yml")
        talkConfig = YamlConfig(dataFolder, "talk.yml")

        shortLang.forEach { lang ->
            languages[lang] = YamlConfig(dataFolder, "languages/${lang}.yml")
        }
        Settings.load()
        Messages.load()
        Talk.load()

    }

    private fun checkUpdate() {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val updateChecker = UpdateChecker(
                        instance,
                        UpdateCheckSource.GITHUB_RELEASE_TAG,
                        "Vallerian/MelodyMine"
                    )
                    updateChecker.checkNow().onSuccess { _, _ ->
                        updateChecker.setDownloadLink("https://github.com/Vallerian/MelodyMine/releases")
                        updateChecker.checkEveryXHours(24.0)
                        updateChecker.setChangelogLink("https://github.com/Vallerian/MelodyMine/releases/tag/${updateChecker.latestVersion}")
                        updateChecker.setNotifyOpsOnJoin(true)
                        updateChecker.setNotifyByPermissionOnJoin("melodymine.updatechecker")
                        updateChecker.setTimeout(30 * 1000)
                        updateChecker.setSupportLink("https://discord.com/users/403446004193558531")
                    }
                } catch (_: Exception) {
                    logger.warning("Could not check for updates, check your connection.")
                }
            }
        }.runTaskAsynchronously(this)

    }


    companion object {
        lateinit var instance: MelodyMine
    }
}
