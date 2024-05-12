package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Utils
import org.bukkit.entity.Player

class Toggle : SubCommand() {


    override var name = "toggle"
    override var description = Messages.getMessageString("commands.toggle.description")
    override var syntax = "/melodymine toggle"
    override var permission = "melodymine.toggle"

    override fun handler(player: Player, args: Array<out String>) {

        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return
        if (Utils.checkPlayerCoolDown(player)) return

        if (melodyPlayer.isToggle) {
            MelodyManager.toggleLogger(melodyPlayer.uuid)
            player.sendMessage(Messages.getMessage("commands.toggle.disable"))
        } else {
            MelodyManager.toggleLogger(melodyPlayer.uuid)
            player.sendMessage(Messages.getMessage("commands.toggle.enable"))
        }
        Utils.resetPlayerCoolDown(player)
    }
}