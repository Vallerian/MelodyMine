package ir.taher7.melodymine.utils

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.storage.Messages
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
                Placeholder.parsed("prefix", Messages.getMessageString("general.prefix")),
                Placeholder.parsed("text_color", Messages.getMessageString("general.text_color")),
                Placeholder.parsed("hover_color", Messages.getMessageString("general.hover_color")),
                Placeholder.parsed("highlight_color", Messages.getMessageString("general.highlight_color")),
            ),
        ).build()
    }

    private fun Player.audience(): Audience {
        return audience.player(this)
    }


    fun CommandSender.sendMessage(component: Component) {
        audience.sender(this).sendMessage(component)
    }


    fun Player.sendActionbar(component: Component) {
        this.audience().sendActionBar(component)
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