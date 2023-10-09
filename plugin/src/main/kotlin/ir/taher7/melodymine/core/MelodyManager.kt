package ir.taher7.melodymine.core

import ir.taher7.melodymine.api.events.*
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.services.Websocket
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.AdventureUtils.sendMessage
import ir.taher7.melodymine.utils.AdventureUtils.toComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player


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
                "name" to playerName,
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
                "name" to playerName,
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

    fun setVolume(playerUuid: String, volume: Double, targetSocketID: String) {
        val preSetVolumeEvent = PreSetVolumeEvent(playerUuid, volume, targetSocketID)
        Bukkit.getServer().pluginManager.callEvent(preSetVolumeEvent)
        if (preSetVolumeEvent.isCancelled) return

        Websocket.socket.emit(
            "onSetVolumePlugin", mapOf<String, Any>(
                "uuid" to playerUuid,
                "volume" to volume,
                "socketID" to targetSocketID
            )
        )

        Bukkit.getServer().pluginManager.callEvent(PostSetVolumeEvent(playerUuid, volume, targetSocketID))
    }
}