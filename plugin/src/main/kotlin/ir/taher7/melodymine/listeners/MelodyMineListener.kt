package ir.taher7.melodymine.listeners

import io.socket.client.SocketIOException
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.services.Websocket
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.sql.Timestamp

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
                result.player = event.player
                result.serverIp = event.player.address?.address?.hostAddress
                result.serverIsOnline = true
                result.server = Storage.server
                result.verifyCode = Utils.getVerifyCode()
                result.serverLastLogin = Timestamp(System.currentTimeMillis())
                Database.updatePlayer(result, false)
                Utils.forceVoice(result)
                Storage.onlinePlayers[result.uuid] = result

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
        object : BukkitRunnable() {
            override fun run() {
                Database.findPlayer(event.player.uniqueId.toString()) { result ->
                    if (result != null && Bukkit.getPlayerExact(result.name) == null) {
                        result.serverIsOnline = false
                        result.verifyCode = Utils.getVerifyCode()
                        result.serverLastLogout = Timestamp(System.currentTimeMillis())
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
        if (!Storage.forceVoice || event.player.player?.hasPermission("melodymine.force") == true) return

        val melodyPlayer = Storage.onlinePlayers[event.player.uniqueId.toString()]
        if (melodyPlayer != null) {
            if (!melodyPlayer.isActiveVoice) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerSendMessage(event: AsyncPlayerChatEvent) {
        if (!Storage.forceVoice || event.player.player?.hasPermission("melodymine.force") == true) return

        val melodyPlayer = Storage.onlinePlayers[event.player.uniqueId.toString()]
        if (melodyPlayer != null) {
            if (!melodyPlayer.isActiveVoice) {
                event.isCancelled = true
            }
        }
    }
}

