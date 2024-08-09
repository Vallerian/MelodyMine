package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendComponent
import ir.taher7.melodymine.utils.Utils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Toggle : SubCommand() {


    override var name = "toggle"
    override var description = Messages.getMessage("commands.toggle.description")
    override var syntax = "/melodymine toggle"
    override var permission = "melodymine.toggle"

    override fun handler(player: CommandSender, args: Array<out String>) {
        if (player !is Player) {
            player.sendComponent(Messages.getMessage("errors.only_players"))
            return
        }

        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
        if (Utils.checkPlayerCoolDown(player)) return

        if (melodyPlayer.isToggle) {
            MelodyManager.toggleLogger(melodyPlayer.uuid)
            player.sendComponent(Messages.getMessage("commands.toggle.disable"))
        } else {
            MelodyManager.toggleLogger(melodyPlayer.uuid)
            player.sendComponent(Messages.getMessage("commands.toggle.enable"))
        }
        Utils.resetPlayerCoolDown(player)
    }
}