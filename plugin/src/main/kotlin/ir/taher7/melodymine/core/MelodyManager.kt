package ir.taher7.melodymine.core

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.api.events.*
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.services.Websocket
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.AdventureUtils.sendMessage
import ir.taher7.melodymine.utils.AdventureUtils.toComponent
import ir.taher7.melodymine.utils.QRCodeRenderer
import ir.taher7.melodymine.utils.Utils
import net.glxn.qrgen.javase.QRCode
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable


object MelodyManager {
    fun mute(uuid: String) {
        val targetPlayer = Storage.onlinePlayers[uuid] ?: return

        val prePlayerMuteEvent = PrePlayerMuteEvent(targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(prePlayerMuteEvent)
        if (prePlayerMuteEvent.isCancelled) return

        targetPlayer.isMute = true
        Database.updatePlayer(targetPlayer, false)
        Websocket.socket.emit(
            "onPlayerMutePlugin", mapOf(
                "uuid" to targetPlayer.uuid
            )
        )

        Bukkit.getServer().pluginManager.callEvent(PostPlayerMuteEvent(targetPlayer))
    }

    fun unMute(uuid: String) {
        val targetPlayer = Storage.onlinePlayers[uuid] ?: return

        val prePlayerUnMuteEvent = PreUnMutePlayerEvent(targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(prePlayerUnMuteEvent)
        if (prePlayerUnMuteEvent.isCancelled) return

        targetPlayer.isMute = false
        Database.updatePlayer(targetPlayer, false)
        Websocket.socket.emit(
            "onPlayerUnmutePlugin", mapOf(
                "uuid" to targetPlayer.uuid
            )
        )

        Bukkit.getServer().pluginManager.callEvent(PostUnMutePlayerEvent(targetPlayer))
    }

    fun sendStartLink(player: Player) {
        Database.getVerifyCode(player) { result ->
            player.sendMessage("<click:open_url:'${Storage.website}/login?verifyCode=${result}'><hover:show_text:'<hover_text>Click to open'><prefix>${Storage.websiteMessage}</hover></click>".toComponent())
        }
    }

    fun sendStartQRCode(player: Player, slot: Int, offhand: Boolean = false) {
        Database.getVerifyCode(player) { result ->
            object : BukkitRunnable() {
                override fun run() {
                    val preSendQRCodeEvent = PreSendQRCodeEvent(player)
                    Bukkit.getServer().pluginManager.callEvent(preSendQRCodeEvent)
                    if (!preSendQRCodeEvent.isCancelled) {

                        val view = Bukkit.createMap(player.world)
                        view.renderers.clear()
                        view.addRenderer(
                            QRCodeRenderer(
                                QRCode.from("${Storage.website}/login?verifyCode=${result}").file()
                            )
                        )

                        val map = Utils.createQRCodeMap(view)
                        if (offhand) {
                            player.inventory.setItemInOffHand(map)
                        } else {
                            player.inventory.setItem(slot, map)
                            player.inventory.heldItemSlot = slot
                        }

                        player.sendMessage("<prefix>Scan the QRCode.".toComponent())
                        Bukkit.getServer().pluginManager.callEvent(PostSendQRCodeEvent(player))
                    }
                }
            }.runTask(MelodyMine.instance)
        }
    }

    fun enableAdminMode(uuid: String) {
        val targetPlayer = Storage.onlinePlayers[uuid] ?: return

        val preEnableAdminModeEvent = PreEnableAdminModeEvent(targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(preEnableAdminModeEvent)
        if (preEnableAdminModeEvent.isCancelled) return

        targetPlayer.adminMode = true
        Websocket.socket.emit(
            "onAdminModeEnablePlugin", mapOf(
                "uuid" to uuid,
                "server" to Storage.server
            )
        )

        Bukkit.getServer().pluginManager.callEvent(PostEnableAdminMode(targetPlayer))
    }

    fun disableAdminMode(uuid: String) {
        val targetPlayer = Storage.onlinePlayers[uuid] ?: return

        val preDisableAdminModeEvent = PreDisableAdminModeEvent(targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(preDisableAdminModeEvent)
        if (preDisableAdminModeEvent.isCancelled) return

        Websocket.socket.emit(
            "onAdminModeDisablePlugin", mapOf(
                "uuid" to uuid,
                "server" to Storage.server
            )
        )

        targetPlayer.adminMode = false
        targetPlayer.isSendOffer = arrayListOf()
        Storage.onlinePlayers.values.forEach { item ->
            if (item.isSendOffer.contains(targetPlayer.uuid)) {
                item.isSendOffer.remove(targetPlayer.uuid)
            }
        }
        Bukkit.getServer().pluginManager.callEvent(PostDisableAdminMode(targetPlayer))
    }

    fun enableLogger(uuid: String) {
        val targetPlayer = Storage.onlinePlayers[uuid] ?: return
        targetPlayer.isToggle = true
    }

    fun disableLogger(uuid: String) {
        val targetPlayer = Storage.onlinePlayers[uuid] ?: return
        targetPlayer.isToggle = false
    }

    fun enableVoice(playerName: String, playerUuid: String, playerServer: String, targetSocketID: String) {
        val preEnableVoiceEvent = PreEnableVoiceEvent(playerName, playerUuid, playerServer, targetSocketID)
        Bukkit.getServer().pluginManager.callEvent(preEnableVoiceEvent)
        if (preEnableVoiceEvent.isCancelled) return

        Websocket.socket.emit(
            "onEnableVoicePlugin", mapOf(
                "uuid" to playerUuid,
                "server" to playerServer,
                "socketID" to targetSocketID
            )
        )

        Bukkit.getServer().pluginManager.callEvent(
            PostEnableVoiceEvent(
                playerName,
                playerUuid,
                playerServer,
                targetSocketID
            )
        )
    }

    fun disableVoice(playerName: String, playerUuid: String, playerServer: String, targetSocketID: String) {
        val preDisableVoiceEvent = PreDisableVoiceEvent(playerName, playerUuid, playerServer, targetSocketID)
        Bukkit.getServer().pluginManager.callEvent(preDisableVoiceEvent)
        if (preDisableVoiceEvent.isCancelled) return

        Websocket.socket.emit(
            "onDisableVoicePlugin", mapOf(
                "uuid" to playerUuid,
                "server" to playerServer,
                "socketID" to targetSocketID
            )
        )

        Bukkit.getServer().pluginManager.callEvent(
            PostDisableVoiceEvent(
                playerName,
                playerUuid,
                playerServer,
                targetSocketID
            )
        )
    }

    fun setVolume(
        playerUuid: String,
        volume: Double,
        targetSocketID: String,
    ) {
        val preSetVolumeEvent = PreSetVolumeEvent(playerUuid, volume, targetSocketID)
        Bukkit.getServer().pluginManager.callEvent(preSetVolumeEvent)
        if (preSetVolumeEvent.isCancelled) return

        Websocket.socket.emit(
            "onSetVolumePlugin",
            mapOf(
                "uuid" to playerUuid,
                "volume" to volume,
                "socketID" to targetSocketID,
            )
        )

        Bukkit.getServer().pluginManager.callEvent(PostSetVolumeEvent(playerUuid, volume, targetSocketID))
    }

    fun setPlayerSelfMute(melodyPlayer: MelodyPlayer, value: Boolean) {
        val prePlayerSetSelfMuteEvent = PrePlayerSetSelfMuteEvent(melodyPlayer, value)
        Bukkit.getServer().pluginManager.callEvent(prePlayerSetSelfMuteEvent)
        if (prePlayerSetSelfMuteEvent.isCancelled) return

        melodyPlayer.isSelfMute = value
        Websocket.socket.emit(
            "onSetControlPlugin",
            mapOf(
                "name" to melodyPlayer.name,
                "uuid" to melodyPlayer.uuid,
                "type" to "mic",
                "server" to melodyPlayer.server,
                "value" to value,
            )
        )

        Bukkit.getServer().pluginManager.callEvent(PostPlayerSetSelfMuteEvent(melodyPlayer, value))
    }

    fun setPlayerDeafen(melodyPlayer: MelodyPlayer, value: Boolean) {
        val prePlayerSetDeafenEvent = PrePlayerSetDeafenEvent(melodyPlayer, value)
        Bukkit.getServer().pluginManager.callEvent(prePlayerSetDeafenEvent)
        if (prePlayerSetDeafenEvent.isCancelled) return

        melodyPlayer.isDeafen = value
        Websocket.socket.emit(
            "onSetControlPlugin",
            mapOf(
                "name" to melodyPlayer.name,
                "uuid" to melodyPlayer.uuid,
                "type" to "sound",
                "server" to melodyPlayer.server,
                "value" to value,
            )
        )

        Bukkit.getServer().pluginManager.callEvent(PostPlayerSetDeafenEvent(melodyPlayer, value))
    }
}