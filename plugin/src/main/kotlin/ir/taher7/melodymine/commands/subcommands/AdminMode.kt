package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendComponent
import ir.taher7.melodymine.utils.Utils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AdminMode : SubCommand() {
    override var name = "adminmode"
    override var description = Messages.getMessage("commands.adminmode.description")
    override var syntax = "/melodymine adminmode"
    override var permission = "melodymine.adminmode"

    override fun handler(player: CommandSender, args: Array<out String>) {
        if (player !is Player) {
            player.sendComponent(Messages.getMessage("errors.only_players"))
            return
        }

        val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()] ?: return

        if (Utils.checkPlayerCoolDown(player)) return

        if (!melodyPlayer.isActiveVoice) {
            player.sendComponent(Messages.getMessage("errors.active_voice"))
            return
        }

        if (melodyPlayer.adminMode) {
            MelodyManager.disableAdminMode(melodyPlayer.uuid)
            player.sendComponent(Messages.getMessage("commands.adminmode.disable"))
        } else {
            MelodyManager.enableAdminMode(melodyPlayer.uuid)
            player.sendComponent(Messages.getMessage("commands.adminmode.enable"))
        }
        Utils.resetPlayerCoolDown(player)
    }
}
