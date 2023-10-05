package ir.taher7.melodymine.utils

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.storage.Storage
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object AdventureUtils {

    private val audience = BukkitAudiences.create(MelodyMine.instance)
    private lateinit var miniMessage: MiniMessage


    init {
        initMiniMessage()
    }

    fun initMiniMessage() {
        miniMessage = MiniMessage.builder().tags(
            TagResolver.resolver(
                TagResolver.standard(),
                Placeholder.parsed("prefix", "${Storage.prefix} <text>"),
                Placeholder.parsed("text_prefix", "<gradient:#F04FE7:#DDB216>"),
                Placeholder.parsed("text", "<gradient:#F04FE7:#FFF4E4>"),
                Placeholder.parsed("hover_text", "<gradient:#F04FE7:#DDB216>")
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

}