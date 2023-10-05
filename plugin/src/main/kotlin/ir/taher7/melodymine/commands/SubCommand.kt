package ir.taher7.melodymine.commands

import org.bukkit.entity.Player

abstract class SubCommand {

    abstract var name: String
    abstract var description: String
    abstract var syntax: String
    abstract var permission: String

    abstract fun handler(player: Player, args: Array<out String>)

}