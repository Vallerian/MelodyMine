package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.AdventureUtils.sendMessage
import ir.taher7.melodymine.utils.AdventureUtils.toComponent
import ir.taher7.melodymine.utils.Utils
import org.bukkit.entity.Player
import java.util.*

class Start : SubCommand() {

    private val coolDown = hashMapOf<UUID, Long>()

    override var name = "start"
    override var description = Storage.startDescription
    override var syntax = "/melodymine start <link | qrcode>"
    override var permission = "melodymine.start"

    override fun handler(player: Player, args: Array<out String>) {

        if (coolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - coolDown[player.uniqueId]!!) <= 5000) {
            player.sendMessage("<prefix>You can use this command after <#DDB216>${((5000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)}</#DDB216> second.".toComponent())
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
                    return
                }

                Utils.sendHelpMessage(player)
            }

            else -> {
                Utils.sendHelpMessage(player)
            }
        }
    }
}