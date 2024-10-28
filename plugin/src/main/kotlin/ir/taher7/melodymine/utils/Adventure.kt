package ir.taher7.melodymine.utils

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.utils.Utils.parsePlaceholder
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.title.Title
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Adventure {
    private val audience = BukkitAudiences.create(MelodyMine.instance)
    private lateinit var miniMessage: MiniMessage

    init {
        initMiniMessage()
    }

    fun initMiniMessage() {
        miniMessage = MiniMessage.builder().tags(
            TagResolver.resolver(
                TagResolver.standard(),
                Placeholder.parsed("prefix", Messages.getMessage("general.prefix")),
                Placeholder.parsed("text_color", Messages.getMessage("general.text_color")),
                Placeholder.parsed("hover_color", Messages.getMessage("general.hover_color")),
                Placeholder.parsed("highlight_color", Messages.getMessage("general.highlight_color")),
            ),
        ).build()
    }

    private fun Player.audience(): Audience {
        return audience.player(this)
    }

    fun CommandSender.sendComponent(string: String) {
        val parseString = parsePlaceholder(if (this is Player) this else null, string)
        audience.sender(this).sendMessage(parseString.toComponent())
    }

    fun Player.sendActionbar(string: String) {
        val parseString = parsePlaceholder(this, string)
        this.audience().sendActionBar(parseString.toComponent())
    }

    fun String.toComponent(vararg placeholders: TagResolver): Component {
        return miniMessage.deserialize(this, *placeholders)
    }

    fun Player.showTitle(title: Title) {
        audience.sender(this).showTitle(title)
    }

    fun Player.showBossBar(bossBar: BossBar) {
        audience.sender(this).showBossBar(bossBar)
    }

    fun Player.hideBossBar(bossBar: BossBar) {
        audience.sender(this).hideBossBar(bossBar)
    }
}
