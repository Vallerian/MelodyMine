package ir.taher7.melodymine.config

import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.bukkit.pluginDirectory
import org.sayandev.stickynote.core.configuration.Config
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting
import java.io.File

data class StorageConfig(
    val database: Database = Database(),
    val websocket: Websocket = Websocket()
): Config(pluginDirectory, FILE_NAME) {
    enum class DatabaseMethod {
        MYSQL,
    }

    @ConfigSerializable
    data class Database(
        val method: DatabaseMethod = DatabaseMethod.MYSQL,
        val host: String = "localhost",
        val port: Int = 3306,
        val username: String = "root",
        val password: String = "",
        val database: String = plugin.name.lowercase(),
        @Setting("use-ssl") val useSSL: Boolean = false,
        val poolSize: Int = 5
    )

    @ConfigSerializable
    data class Websocket(
        val token: String = "",
        val host: String = "localhost",
        val port: Int = 443,
        @Setting("use-ssl") val useSSL: Boolean = true,
    )

    companion object {
        const val FILE_NAME = "config.yml"
        private val settingsConfigFile = File(pluginDirectory, FILE_NAME)
        private var config = fromConfig() ?: defaultConfig()

        fun get(): SettingsConfig {
            return config
        }

        fun defaultConfig(): SettingsConfig {
            return SettingsConfig().apply { save() }
        }

        fun fromConfig(): SettingsConfig? {
            return fromConfig<SettingsConfig>(settingsConfigFile)
        }

        fun reload() {
            config = fromConfig() ?: defaultConfig()
        }
    }
}