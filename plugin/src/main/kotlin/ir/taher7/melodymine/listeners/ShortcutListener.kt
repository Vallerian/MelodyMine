package ir.taher7.melodymine.listeners

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class ShortcutListener : Listener {

    private val coolDown = hashMapOf<UUID, Long>()

    @EventHandler
    fun onPressShift(event: PlayerToggleSneakEvent) {
        object : BukkitRunnable() {
            override fun run() {
                if (!Storage.muteToggleShortcut) return
                val melodyPlayer = Storage.onlinePlayers[event.player.uniqueId.toString()] ?: return
                if (!melodyPlayer.webIsOnline || !melodyPlayer.isActiveVoice) return
                if (event.isSneaking) {
                    Storage.playerMuteShortcut.add(event.player.uniqueId)
                } else {
                    Storage.playerMuteShortcut.remove(event.player.uniqueId)
                }
            }
        }.runTask(MelodyMine.instance)
    }

    @EventHandler
    fun onSwapItem(event: PlayerSwapHandItemsEvent) {
        object : BukkitRunnable() {
            override fun run() {

                if (!Storage.muteToggleShortcut) return
                val player = event.player
                if (!player.hasPermission("melodymine.control")) return
                val melodyPlayer = Storage.onlinePlayers[event.player.uniqueId.toString()] ?: return
                if (!melodyPlayer.webIsOnline || !melodyPlayer.isActiveVoice) return
                if (!Storage.playerMuteShortcut.contains(event.player.uniqueId)) return
                if (coolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - coolDown[player.uniqueId]!!) <= 1000) {
                    player.sendMessage("<prefix>You can toggle after <count_color>${((1000 - (System.currentTimeMillis() - coolDown[player.uniqueId]!!)) / 1000)}<text> second.".toComponent())
                    return
                }
                MelodyManager.setPlayerSelfMute(melodyPlayer, !melodyPlayer.isSelfMute)
                if (melodyPlayer.isSelfMute) {
                    player.sendMessage(Storage.unMuteToggleMessage.toComponent())
                } else {
                    player.sendMessage(Storage.muteToggleMessage.toComponent())
                }
                coolDown[player.uniqueId] = System.currentTimeMillis()
            }
        }.runTask(MelodyMine.instance)
    }

}