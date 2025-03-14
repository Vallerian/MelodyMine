package ir.taher7.melodymine.config

import org.sayandev.stickynote.bukkit.pluginDirectory
import org.sayandev.stickynote.core.configuration.Config
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.io.File

@ConfigSerializable
data class TalkConfig(
    val bossbar: Bossbar = Bossbar(),
    val nameTag: NameTag = NameTag(),
) : Config(pluginDirectory, FILE_NAME) {

    @ConfigSerializable
    data class Bossbar(
        val enable: Boolean = true,
        val images: BossbarImages = BossbarImages(),
    )

    @ConfigSerializable
    data class BossbarImages(
        val active: BossbarImage = BossbarImage(),
        val inactive: BossbarImage = BossbarImage(
            enable = false,
            color = "white",
            text = ""
        ),
        val selfMute: BossbarImage = BossbarImage(
            enable = true,
            color = "yellow",
            text = "<yellow>\uD83D\uDD07"
        ),
        val serverMute: BossbarImage = BossbarImage(
            enable = true,
            color = "red",
            text = "<red>\uD83D\uDEAB"
        ),
    )

    @ConfigSerializable
    data class BossbarImage(
        var enable: Boolean = true,
        val color: String = "green",
        val text: String = "<green> \uD83C\uDFA4",
    )

    @ConfigSerializable
    data class NameTag(
        val enable: Boolean = true,
        val active: NameTagImage = NameTagImage(),
        val inactive: NameTagImage = NameTagImage(
            enable = false,
            textVisible = true,
            text = "",
            position = NameTagImagePosition(
                x = 0.0,
                y = 2.1,
                z = 0.0,
            ),
            item = NameTagImageItem(
                type = "AIR",
                customData = 0
            )
        ),
        val selfMute: NameTagImage = NameTagImage(
            enable = true,
            textVisible = true,
            text = "<yellow>\uD83D\uDD07",
            position = NameTagImagePosition(
                x = 0.0,
                y = 2.1,
                z = 0.0,
            ),
            item = NameTagImageItem(
                type = "AIR",
                customData = 0
            )
        ),
        val serverMute: NameTagImage = NameTagImage(
            enable = true,
            textVisible = true,
            text = "<red>\uD83D\uDEAB",
            position = NameTagImagePosition(
                x = 0.0,
                y = 2.1,
                z = 0.0,
            ),
            item = NameTagImageItem(
                type = "AIR",
                customData = 0
            )
        ),
    )


    @ConfigSerializable
    data class NameTagImage(
        val enable: Boolean = true,
        val textVisible: Boolean = true,
        val text: String = "<green>\uD83C\uDFA4",
        val position: NameTagImagePosition = NameTagImagePosition(),
        val item: NameTagImageItem = NameTagImageItem(),
    )

    @ConfigSerializable
    data class NameTagImagePosition(
        val x: Double = 0.0,
        val y: Double = 2.1,
        val z: Double = 0.0,
    )

    @ConfigSerializable
    data class NameTagImageItem(
        val type: String = "AIR",
        val customData: Int = 0,
    )

    companion object {
        const val FILE_NAME = "talk.yml"
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