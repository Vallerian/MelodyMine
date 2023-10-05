package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.AdventureUtils
import ir.taher7.melodymine.utils.AdventureUtils.sendMessage
import ir.taher7.melodymine.utils.AdventureUtils.toComponent
import org.bukkit.entity.Player

class Reload : SubCommand() {
    override var name = "reload"
    override var description = Storage.reloadDescription
    override var syntax = "/melodymine reload"
    override var permission = "melodymine.reload"

    override fun handler(player: Player, args: Array<out String>) {
        Storage.reload()
        AdventureUtils.initMiniMessage()
        player.sendMessage("<prefix>Plugin has been successfully reload.</gradient>".toComponent())
    }

}