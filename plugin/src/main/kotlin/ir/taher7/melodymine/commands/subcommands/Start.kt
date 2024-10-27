package ir.taher7.melodymine.commands.subcommands

import com.cryptomorin.xseries.reflection.XReflection
import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.utils.Adventure.sendComponent
import ir.taher7.melodymine.utils.Utils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Start : SubCommand() {
    override var name = "start"
    override var description = Messages.getMessage("commands.start.description")
    override var syntax = "/melodymine start"
    override var permission = "melodymine.start"

    override fun handler(player: CommandSender, args: Array<out String>) {
        if (player !is Player) {
            player.sendComponent(Messages.getMessage("errors.only_players"))
            return
        }

        if (Utils.checkPlayerCoolDown(player)) return

        when (args.size) {
            2 -> {
                if (XReflection.supports(13) && args[1].equals("qrcode", true)) {
                    if (!player.hasPermission("melodymine.qrcode")) {
                        player.sendComponent(Messages.getMessage("errors.no_permission"))
                        return
                    }
//                    if (ReflectionUtils.supports(9) && player.inventory.itemInOffHand.type == Material.AIR) {
//                        MelodyManager.sendStartQRCode(player, 1, true)
//                        coolDown[player.uniqueId] = System.currentTimeMillis()
//                        return
//                    }
                    player.inventory.forEachIndexed { index, itemStack ->
                        if (index in 0..8) {
                            if (itemStack == null) {
                                MelodyManager.sendStartQRCode(player, index)
                                Utils.resetPlayerCoolDown(player)
                                return
                            }
                        }
                    }
                    player.sendComponent(Messages.getMessage("commands.start.hot_bar"))

                }

                if (args[1].equals("link", true)) {
                    MelodyManager.sendStartLink(player)
                    Utils.resetPlayerCoolDown(player)
                    return
                }

                sendStartHelpMessage(player)
            }

            else -> sendStartHelpMessage(player)
        }
    }

    private fun sendStartHelpMessage(player: Player) {
        player.sendComponent(Messages.getMessage("general.content_header"))
        Messages.getHelpMessage(
            "commands.start.help_message",
            hashMapOf("{SYNTAX}" to syntax)
        ).forEach { message ->
            player.sendComponent(message)
        }
        player.sendComponent(Messages.getMessage("general.content_footer"))
    }
}
