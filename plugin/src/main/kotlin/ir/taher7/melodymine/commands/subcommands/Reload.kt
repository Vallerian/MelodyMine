package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.entity.Player

class Reload : SubCommand() {
    override var name = "reload"
    override var description = Storage.reloadDescription
    override var syntax = "/melodymine reload"
    override var permission = "melodymine.reload"

    override fun handler(player: Player, args: Array<out String>) {
        Storage.reload()

        Adventure.initMiniMessage()
        player.sendMessage("<prefix>Plugin has been successfully reload.".toComponent())
        Storage.onlinePlayers.values.forEach { melodyPlayer ->
            if (melodyPlayer.webIsOnline) {
                MelodyManager.sendSoundSetting(melodyPlayer.socketID!!)
            }
            if (melodyPlayer.isActiveVoice) {
                melodyPlayer.talkBossBar?.initTalkBossBar()
            }
        }
    }

}