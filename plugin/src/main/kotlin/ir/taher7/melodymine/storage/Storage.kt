package ir.taher7.melodymine.storage

import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.models.MelodyPlayer
import java.util.*


object Storage {

    val onlinePlayers = hashMapOf<String, MelodyPlayer>()
    val subCommands = ArrayList<SubCommand>()
    val playerMuteShortcut = HashSet<UUID>()
    val commandCoolDown = hashMapOf<UUID, Long>()
    val shortcutCoolDown = hashMapOf<UUID, Long>()

}