package ir.taher7.melodymine.storage

import ir.taher7.melodymine.MelodyMine
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.logging.Level

class YamlConfig(private val folder: File, private val fileName: String) {
    lateinit var config: FileConfiguration
    private lateinit var configFile: File

    init {
        saveDefaultConfig()
        reloadConfig()
    }

    fun reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile)
        val defaultStream: InputStream? = MelodyMine.instance.getResource(fileName)
        if (defaultStream != null) {
            val defaultConfig = YamlConfiguration.loadConfiguration(InputStreamReader(defaultStream))
            (config as YamlConfiguration).setDefaults(defaultConfig)
        }
    }

    fun saveConfig() {
        try {
            config.save(configFile)
        } catch (ex: IOException) {
            MelodyMine.instance.logger.log(Level.SEVERE, "Could not save config to $configFile", ex)
        }
    }

    private fun saveDefaultConfig() {
        configFile = File(folder, fileName)
        if (!configFile.exists()) {
            try {
                MelodyMine.instance.saveResource(fileName, false)
            } catch (ex: IllegalArgumentException) {
                try {
                    configFile.createNewFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }
    }
}
