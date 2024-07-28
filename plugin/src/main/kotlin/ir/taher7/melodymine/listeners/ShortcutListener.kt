package ir.taher7.melodymine.listeners

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Settings
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.scheduler.BukkitRunnable

class ShortcutListener : Listener {


    @EventHandler
    fun onPressShift(event: PlayerToggleSneakEvent) {
        object : BukkitRunnable() {
            override fun run() {
                if (!Settings.shortcut) return
                val melodyPlayer = MelodyManager.getMelodyPlayer(event.player.uniqueId.toString()) ?: return
                if (!melodyPlayer.webIsOnline || !melodyPlayer.isActiveVoice) return

                if (event.isSneaking) {
                    if (Storage.playerMuteShortcut.contains(event.player.uniqueId)) return
                    Storage.playerMuteShortcut.add(event.player.uniqueId)
                } else {
                    if (!Storage.playerMuteShortcut.contains(event.player.uniqueId)) return
                    Storage.playerMuteShortcut.remove(event.player.uniqueId)
                }
            }
        }.runTask(MelodyMine.instance)
    }

    @EventHandler
    fun onSwapItem(event: PlayerSwapHandItemsEvent) {
        object : BukkitRunnable() {
            override fun run() {
                if (!Settings.shortcut) return
                val player = event.player
                if (!player.hasPermission("melodymine.control")) return
                val melodyPlayer = Storage.onlinePlayers[event.player.uniqueId.toString()] ?: return
                if (!melodyPlayer.webIsOnline || !melodyPlayer.isActiveVoice) return
                if (!Storage.playerMuteShortcut.contains(event.player.uniqueId)) return

                if (Storage.shortcutCoolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - Storage.shortcutCoolDown[player.uniqueId]!!) <= Settings.shortcutCoolDown) {
                    player.sendComponent(
                        Messages.getMessage(
                            "commands.control.toggle_cool_down",
                            hashMapOf("{TIME}" to ((Settings.shortcutCoolDown - (System.currentTimeMillis() - Storage.shortcutCoolDown[player.uniqueId]!!)) / 1000))
                        )
                    )
                    return
                }

                MelodyManager.setPlayerSelfMute(melodyPlayer, !melodyPlayer.isSelfMute)
                if (melodyPlayer.isSelfMute) {
                    player.sendComponent(Messages.getMessage("commands.control.unmute"))
                } else {
                    player.sendComponent(Messages.getMessage("commands.control.mute"))
                }
                Storage.shortcutCoolDown[player.uniqueId] = System.currentTimeMillis()
            }
        }.runTask(MelodyMine.instance)
    }

}