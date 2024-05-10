package ir.taher7.melodymine.storage

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.models.BossBarConfig
import ir.taher7.melodymine.models.Item
import ir.taher7.melodymine.models.NameTagConfig
import ir.taher7.melodymine.models.Position

object Talk {

    val bossbarConfigs = hashMapOf<String, BossBarConfig>()
    val nameTagConfigs = hashMapOf<String, NameTagConfig>()

    var isEnableBossBar: Boolean = true
    var isEnableNameTag: Boolean = true

    fun load() {
        val talkSection = MelodyMine.instance.talkConfig.config.getConfigurationSection("talk_configs") ?: return
        val bossBarSection = talkSection.getConfigurationSection("bossbar") ?: return
        val nameTagSection = talkSection.getConfigurationSection("nametag") ?: return

        isEnableBossBar = bossBarSection.getBoolean("enable")
        isEnableNameTag = nameTagSection.getBoolean("enable")

        val bossBarImagesSection = bossBarSection.getConfigurationSection("images") ?: return
        val nameTagImagesSection = nameTagSection.getConfigurationSection("images") ?: return


        for (key in bossBarImagesSection.getKeys(false)) {
            val config = bossBarImagesSection.getConfigurationSection(key) ?: continue
            bossbarConfigs[key] = BossBarConfig(
                enable = config.getBoolean("enable"),
                color = config.getString("color") ?: "white",
                text = config.getString("text") ?: ""
            )
        }

        for (key in nameTagImagesSection.getKeys(false)) {
            val config = nameTagImagesSection.getConfigurationSection(key) ?: continue
            nameTagConfigs[key] = NameTagConfig(
                enable = config.getBoolean("enable"),
                textVisible = config.getBoolean("text_visible"),
                text = config.getString("text") ?: "",
                position = Position(
                    x = config.getConfigurationSection("position")?.getDouble("x") ?: 0.0,
                    y = config. getConfigurationSection("position")?.getDouble("y") ?: 2.1,
                    z = config.getConfigurationSection("position")?.getDouble("z") ?: 0.0,
                ),
                item = Item(
                    type = config.getConfigurationSection("item")?.getString("type") ?: "AIR",
                    customData = config.getConfigurationSection("item")?.getInt("custom_data") ?: 0,
                )
            )
        }
    }
}