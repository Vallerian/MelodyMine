package ir.taher7.melodymine.commands.subcommands

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Settings
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.storage.Talk
import ir.taher7.melodymine.utils.Adventure
import ir.taher7.melodymine.utils.Adventure.sendComponent
import org.bukkit.command.CommandSender

class Reload : SubCommand() {
    override var name = "reload"
    override var description = Messages.getMessage("commands.reload.description")
    override var syntax = "/melodymine reload"
    override var permission = "melodymine.reload"

    override fun handler(player: CommandSender, args: Array<out String>) {
        Storage.onlinePlayers.values.forEach { melodyPlayer ->
            melodyPlayer.talkBossBar?.hideTalkBossBar()
        }

        MelodyMine.instance.settingsConfig.reloadConfig()
        MelodyMine.instance.databaseConfig.reloadConfig()
        MelodyMine.instance.talkConfig.reloadConfig()
        MelodyMine.instance.languages.values.forEach { lang ->
            lang.reloadConfig()
        }
        Settings.load()
        Messages.load()
        Talk.load()

        Adventure.initMiniMessage()
        player.sendComponent(Messages.getMessage("commands.reload.reload_success"))
        Storage.onlinePlayers.values.forEach { melodyPlayer ->
            if (melodyPlayer.webIsOnline) MelodyManager.sendSoundSetting(melodyPlayer.socketID!!)
            if (melodyPlayer.isActiveVoice) melodyPlayer.talkBossBar?.initTalkBossBar()
        }
    }
}
