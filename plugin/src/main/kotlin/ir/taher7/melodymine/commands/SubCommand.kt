package ir.taher7.melodymine.commands

import org.bukkit.command.CommandSender

abstract class SubCommand {
    abstract var name: String
    abstract var description: String
    abstract var syntax: String
    abstract var permission: String
    abstract fun handler(sender: CommandSender, args: Array<out String>)
}
