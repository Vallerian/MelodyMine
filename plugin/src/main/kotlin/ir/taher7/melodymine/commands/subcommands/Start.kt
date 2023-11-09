package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
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
            player.sendMessage("<prefix>You can use this command after <count_color>${((5000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)}<text> second.".toComponent())
            return
        }
        when (args.size) {
            2 -> {
                if (args[1].equals("qrcode", true)) {
                    if (!player.hasPermission("melodymine.qrcode")) {
                        player.sendMessage("<prefix>You dont have permission to use this command.".toComponent())
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
                                coolDown[player.uniqueId] = System.currentTimeMillis()
                                return
                            }
                        }
                    }
                    player.sendMessage("<prefix>HotBar is full!".toComponent())

                }

                if (args[1].equals("link", true)) {
                    MelodyManager.sendStartLink(player)
                    coolDown[player.uniqueId] = System.currentTimeMillis()
                    return
                }

                sendStartHelpMessage(player)
            }

            else -> {
                sendStartHelpMessage(player)
            }
        }
    }

    private fun sendStartHelpMessage(player: Player) {
        player.sendMessage(Storage.contentHeader.toComponent())
        player.sendMessage("")
        player.sendMessage("<click:run_command:'${syntax} link'><hover:show_text:'<text_hover>Click to run <i>${syntax} link</i>'><text_hover>${syntax} link <#FFF4E4><bold>|</bold> <text>Open Website with Link.</hover></click>".toComponent())
        player.sendMessage("<click:run_command:'${syntax} qrcode'><hover:show_text:'<text_hover>Click to run <i>${syntax} qrcode</i>'><text_hover>${syntax} qrcode <#FFF4E4><bold>|</bold> <text>Open Website with Qrcode.</hover></click>".toComponent())
        player.sendMessage("")
        player.sendMessage(Storage.contentFooter.toComponent())
    }
}