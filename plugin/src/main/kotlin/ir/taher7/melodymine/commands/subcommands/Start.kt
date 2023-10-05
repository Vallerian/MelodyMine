package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.AdventureUtils.sendMessage
import ir.taher7.melodymine.utils.AdventureUtils.toComponent
import org.bukkit.entity.Player
import java.util.*

class Start : SubCommand() {

    private val coolDown = hashMapOf<UUID, Long>()

    override var name = "start"
    override var description = Storage.startDescription
    override var syntax = "/melodymine start"
    override var permission = "melodymine.start"

    override fun handler(player: Player, args: Array<out String>) {

        if (coolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - coolDown[player.uniqueId]!!) <= 5000) {
            player.sendMessage("<prefix>You can use this command after <#DDB216>${((5000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)}</#DDB216> second.".toComponent())
            return
        }
        MelodyManager.sendStartLink(player)
        coolDown[player.uniqueId] = System.currentTimeMillis()
    }
}