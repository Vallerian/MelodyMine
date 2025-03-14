package ir.taher7.melodymine.config

import ir.taher7.melodymine.config.LanguageConfig.Language
import org.sayandev.stickynote.bukkit.pluginDirectory
import org.sayandev.stickynote.core.configuration.Config
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting
import java.io.File

@ConfigSerializable
data class SettingsConfig(
    val language: Language = Language.EN_US,
    val initialConfigs: InitialConfig = InitialConfig(),
    val autoStart: Boolean = true,
    val forceVoice: ForceVoice = ForceVoice(),
    val muteToggleShortcut: MuteToggleShortcut = MuteToggleShortcut(),
    val callPendingTime: Long = 600,
    val soundConfigs: SoundConfigs = SoundConfigs(),
    val renewConfigs: RenewConfigs = RenewConfigs(),

    ) : Config(pluginDirectory, FILE_NAME) {

    @ConfigSerializable
    data class InitialConfig(
        val server: String = "Lobby",
        val domain: String = "localhost",
        @Setting("use-ssl") val useSSL: Boolean = false,
        val serverDomain: String = "",
        val clientPort: Int = 3000,
        val serverPort: Int = 4000,
        val pluginKey: String = "",
    )

    @ConfigSerializable
    data class ForceVoice(
        val enable: Boolean = true,
        val sendTitle: Boolean = false,
        val sendInterval: Long = 300,
        val damage: Boolean = false,
    )

    @ConfigSerializable
    data class MuteToggleShortcut(
        val enable: Boolean = true,
        val coolDown: Int = 1000,
    )

    @ConfigSerializable
    data class SoundConfigs(
        val hearLazy: Boolean = true,
        val maxDistance: Int = 15,
        val refDistance: Int = 5,
        val rolloffFactor: Int = 1,
        val innerAngle: Int = 120,
        val outerAngle: Int = 180,
        val outerVolume: Float = 0.3f,
    )

    @ConfigSerializable
    data class RenewConfigs(
        val interval: Long = 30,
        val connectDistance: Int = 15,
        val volumeDistance: Int = 30,
        val disconnectDistance: Int = 80,
    )

    companion object {
        const val FILE_NAME = "settings.yml"
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