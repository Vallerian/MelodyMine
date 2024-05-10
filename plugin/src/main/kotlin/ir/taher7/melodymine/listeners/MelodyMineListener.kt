package ir.taher7.melodymine.listeners

import io.socket.client.SocketIOException
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.core.TalkBossBar
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.services.Websocket
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.core.TalkNameTag
import ir.taher7.melodymine.storage.Settings
import ir.taher7.melodymine.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable


class MelodyMineListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        Database.findPlayer(event.player.uniqueId.toString()) { result ->
            if (result == null) {
                Database.initPlayer(event.player) { newUser ->
                    Storage.onlinePlayers[newUser.uuid] = newUser
                    Utils.forceVoice(newUser)
                }
            } else {
                if (!result.serverIsOnline) Utils.forceVoice(result)
                result.player = event.player
                result.serverIsOnline = true
                result.server = Settings.server
                result.verifyCode = Utils.getVerifyCode()
                Database.updatePlayer(result, false)
                Storage.onlinePlayers[result.uuid] = result
                Storage.onlinePlayers[result.uuid]?.talkBossBar = TalkBossBar(event.player)
                Storage.onlinePlayers[result.uuid]?.talkNameTag = TalkNameTag(event.player)
                MelodyManager.checkPlayerWebConnection(result)
                if (result.webIsOnline && result.isActiveVoice) {
                    Storage.onlinePlayers.values.forEach { player ->
                        if (player.uuid != result.uuid && player.adminMode && player.socketID != null) {
                            Websocket.socket.emit(
                                "onPlayerInitAdminModePlugin", mapOf(
                                    "name" to player.name,
                                    "uuid" to player.uuid,
                                    "server" to player.server,
                                    "socketID" to result.socketID
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        Utils.removePlayerCoolDown(event.player)
        Storage.shortcutCoolDown.remove(event.player.uniqueId)
        object : BukkitRunnable() {
            override fun run() {
                Database.findPlayer(event.player.uniqueId.toString()) { result ->
                    if (result != null && Bukkit.getPlayerExact(result.name) == null) {
                        result.serverIsOnline = false
                        result.verifyCode = Utils.getVerifyCode()
                        Database.updatePlayer(result, true)
                        if (result.isActiveVoice && result.webIsOnline) {
                            val data = mutableMapOf<String, String>()
                            data["name"] = result.name
                            data["uuid"] = result.uuid
                            data["server"] = result.server

                            try {
                                Websocket.socket.emit("onPlayerLeavePlugin", data)
                            } catch (ex: SocketIOException) {
                                ex.printStackTrace()
                            }

                            Storage.onlinePlayers[result.uuid]?.talkNameTag?.clearNameTag()
                            Storage.onlinePlayers.remove(result.uuid)
                            Storage.onlinePlayers.values.forEach { player ->
                                if (player.isSendOffer.contains(result.uuid)) {
                                    player.isSendOffer.remove(result.uuid)
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskLater(MelodyMine.instance, 60L)
    }


    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val melodyPlayer = Storage.onlinePlayers[event.player.uniqueId.toString()] ?: return
        if (!melodyPlayer.isActiveVoice) {
            if (Utils.checkPlayerForce(melodyPlayer)) return
            val from = event.from
            val to = event.to ?: return
            if (from.blockX != to.blockX || from.blockY != to.blockY || from.blockZ != to.blockZ) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerSendMessage(event: AsyncPlayerChatEvent) {
        val melodyPlayer = Storage.onlinePlayers[event.player.uniqueId.toString()] ?: return
        if (Utils.checkPlayerForce(melodyPlayer)) return
        if (!melodyPlayer.isActiveVoice) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerChangeWorld(event: PlayerChangedWorldEvent) {
        val melodyPlayer = Storage.onlinePlayers[event.player.uniqueId.toString()] ?: return
        Utils.forceVoice(melodyPlayer)
        if (!melodyPlayer.isActiveVoice || melodyPlayer.adminMode) return

        if (melodyPlayer.isInCall && Settings.disableWorlds.contains(event.player.world.name)) {
            melodyPlayer.callTarget?.let { MelodyManager.endCall(melodyPlayer, it) }
        }

        melodyPlayer.isSendOffer.forEach { uuid ->
            val targetPlayer = Storage.onlinePlayers[uuid]
            if (targetPlayer != null) {
                if (!targetPlayer.adminMode && !targetPlayer.isInCall) {
                    val targetSocketID = targetPlayer.socketID
                    if (targetSocketID != null) {
                        object : BukkitRunnable() {
                            override fun run() {
                                MelodyManager.disableVoice(
                                    melodyPlayer.name,
                                    melodyPlayer.uuid,
                                    melodyPlayer.server,
                                    targetSocketID
                                )
                                melodyPlayer.isSendOffer.remove(targetPlayer.uuid)
                                targetPlayer.isSendOffer.remove(melodyPlayer.uuid)
                            }
                        }.runTask(MelodyMine.instance)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (!Settings.forceVoice) return
        if (!Settings.forceVoiceDamage) return
        if (event.entity !is Player) return
        if (event.entity.hasPermission("melodymine.force")) return
        if (Settings.disableWorlds.contains(event.entity.location.world?.name)) return
        val melodyPlayer = Storage.onlinePlayers[event.entity.uniqueId.toString()] ?: return
        if (melodyPlayer.isActiveVoice) return
        event.isCancelled = true
    }

}

