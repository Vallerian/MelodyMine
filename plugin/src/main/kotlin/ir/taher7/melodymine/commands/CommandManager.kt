package ir.taher7.melodymine.commands

import ir.taher7.melodymine.commands.subcommands.*
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendComponent
import ir.taher7.melodymine.utils.Utils.sendHelpMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandManager : CommandExecutor {

    init {
        Storage.subCommands.add(Reload())
        Storage.subCommands.add(Start())
        Storage.subCommands.add(AdminMode())
        Storage.subCommands.add(Toggle())
        Storage.subCommands.add(Mute())
        Storage.subCommands.add(Unmute())
        Storage.subCommands.add(Control())
        Storage.subCommands.add(Call())
        Storage.subCommands.add(Status())
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {


        if (args.isEmpty()) {
            if (sender !is Player) {
                sender.sendComponent(Messages.getMessage("errors.only_players"))
                return true
            }
            sendHelpMessage(sender as Player)
            return true
        }

        Storage.subCommands.forEach { subCommand ->
            if (args[0].equals(subCommand.name, true)) {
                if (sender.hasPermission(subCommand.permission)) {
                    subCommand.handler(sender, args)
                } else {
                    sender.sendComponent(Messages.getMessage("errors.no_permission"))
                }
            }
        }

        return true
    }

}