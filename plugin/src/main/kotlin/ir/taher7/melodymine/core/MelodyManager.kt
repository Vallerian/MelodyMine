package ir.taher7.melodymine.core

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.api.events.*
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.services.Websocket
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.toComponent
import ir.taher7.melodymine.utils.QRCodeRenderer
import ir.taher7.melodymine.utils.Utils
import net.glxn.qrgen.javase.QRCode
import org.bukkit.Bukkit
import org.bukkit.Location
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
            player.sendMessage("<click:open_url:'${Storage.website}/login?verifyCode=${result}'><hover:show_text:'<text_hover>Click to open'><prefix>${Storage.websiteMessage}</hover></click>".toComponent())
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
        targetSocketID: String,
        playerLocation: Location,
        targetLocation: Location,
    ) {
        val preSetVolumeEvent = PreSetVolumeEvent(playerUuid, targetSocketID)
        Bukkit.getServer().pluginManager.callEvent(preSetVolumeEvent)
        if (preSetVolumeEvent.isCancelled) return

        Websocket.socket.emit(
            "onSetVolumePlugin",
            mapOf(
                "uuid" to playerUuid,
                "distance" to playerLocation.distance(targetLocation),
                "socketID" to targetSocketID,
                "settings" to mapOf(
                    "sound3D" to Storage.sound3D,
                    "lazyHear" to Storage.lazyHear,
                    "maxDistance" to Storage.maxDistance,
                    "refDistance" to Storage.refDistance,
                    "innerAngle" to Storage.innerAngle,
                    "outerAngle" to Storage.outerAngle,
                    "outerVolume" to Storage.outerVolume,
                ),
                "playerLocation" to mapOf(
                    "x" to playerLocation.x,
                    "y" to playerLocation.y,
                    "z" to playerLocation.z,
                ),
                "targetLocation" to mapOf(
                    "x" to targetLocation.x,
                    "y" to targetLocation.y,
                    "z" to targetLocation.z,
                ),
                "playerDirection" to mapOf(
                    "x" to playerLocation.direction.x,
                    "y" to playerLocation.direction.y,
                    "z" to playerLocation.direction.z,
                ),
                "targetDirection" to mapOf(
                    "x" to targetLocation.direction.x,
                    "y" to targetLocation.direction.y,
                    "z" to targetLocation.direction.z,
                )
            )
        )

        Bukkit.getServer().pluginManager.callEvent(PostSetVolumeEvent(playerUuid, targetSocketID))
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

    fun startCall(melodyPlayer: MelodyPlayer, targetPlayer: MelodyPlayer) {

        val preStartCallEvent = PreStartCallEvent(melodyPlayer, targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(preStartCallEvent)
        if (preStartCallEvent.isCancelled) return

        if (preStartCallEvent.canSendMessage) {
            preStartCallEvent.canSendMessage = true
            melodyPlayer.player?.sendMessage(Storage.contentHeader.toComponent())
            melodyPlayer.player?.sendMessage("")
            melodyPlayer.player?.sendMessage("<text>Please wait to <count_color>${targetPlayer.name} <text>Accept Call.".toComponent())
            melodyPlayer.player?.sendMessage("<click:run_command:'/melodymine call deny'><text>Click to <gradient:#D80032:#F78CA2>Deny</gradient> <text>Call.</click>".toComponent())
            melodyPlayer.player?.sendMessage("")
            melodyPlayer.player?.sendMessage(Storage.contentFooter.toComponent())
        }

        melodyPlayer.isStartCall = true
        melodyPlayer.isCallPending = true
        melodyPlayer.callPendingTarget = targetPlayer
        targetPlayer.isCallPending = true
        targetPlayer.callPendingTarget = melodyPlayer

        val scheduler = MelodyMine.instance.server.scheduler
        val callTask = scheduler.runTaskLater(MelodyMine.instance, Runnable {
            val newMelodyPlayer = Storage.onlinePlayers[melodyPlayer.uuid] ?: return@Runnable
            val newTargetPlayer = Storage.onlinePlayers[targetPlayer.uuid] ?: return@Runnable

            if ((newMelodyPlayer.isCallPending && newMelodyPlayer.callPendingTarget == newTargetPlayer) &&
                (newTargetPlayer.isCallPending && newTargetPlayer.callPendingTarget == newMelodyPlayer)
            ) {
                endPendingCall(newMelodyPlayer, newTargetPlayer)
            }
        }, Storage.callPendingTime)

        melodyPlayer.pendingTask = callTask
        targetPlayer.pendingTask = callTask

        Websocket.socket.emit(
            "onStartCallPlugin",
            mapOf(
                "player" to mapOf(
                    "name" to targetPlayer.name,
                    "uuid" to targetPlayer.uuid,
                    "socketID" to melodyPlayer.socketID,
                ),
                "target" to mapOf(
                    "name" to melodyPlayer.name,
                    "uuid" to melodyPlayer.uuid,
                    "socketID" to targetPlayer.socketID,
                ),
            )
        )

        if (preStartCallEvent.canSendMessage) {
            preStartCallEvent.canSendMessage = true
            targetPlayer.player?.sendMessage(Storage.contentHeader.toComponent())
            targetPlayer.player?.sendMessage("")
            targetPlayer.player?.sendMessage("<text>You have Received a Call from <count_color>${melodyPlayer.name}<text>.".toComponent())
            targetPlayer.player?.sendMessage("<click:run_command:'/melodymine call accept'><text>Click to <gradient:#16FF00:#A2FF86>Accept</gradient> <text>Call.</click>".toComponent())
            targetPlayer.player?.sendMessage("<click:run_command:'/melodymine call deny'><text>Click to <gradient:#D80032:#F78CA2>Deny</gradient> <text>Call.</click>".toComponent())
            targetPlayer.player?.sendMessage("")
            targetPlayer.player?.sendMessage(Storage.contentFooter.toComponent())
        }

        Bukkit.getServer().pluginManager.callEvent(PostStartCallEvent(melodyPlayer, targetPlayer))
    }

    fun endCall(melodyPlayer: MelodyPlayer, targetPlayer: MelodyPlayer) {

        val preEndCallEvent = PreEndCallEvent(melodyPlayer, targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(preEndCallEvent)
        if (preEndCallEvent.isCancelled) return

        if (melodyPlayer.isSendOffer.contains(targetPlayer.uuid)) {
            melodyPlayer.isSendOffer.remove(targetPlayer.uuid)
        }

        if (targetPlayer.isSendOffer.contains(melodyPlayer.uuid)) {
            targetPlayer.isSendOffer.remove(melodyPlayer.uuid)
        }

        melodyPlayer.isInCall = false
        melodyPlayer.callTarget = null
        melodyPlayer.isStartCall = false

        targetPlayer.isInCall = false
        targetPlayer.callTarget = null
        targetPlayer.isStartCall = false


        Websocket.socket.emit(
            "onEndCallPlugin",
            mapOf(
                "player" to mapOf(
                    "name" to targetPlayer.name,
                    "uuid" to targetPlayer.uuid,
                    "socketID" to melodyPlayer.socketID,
                ),
                "target" to mapOf(
                    "name" to melodyPlayer.name,
                    "uuid" to melodyPlayer.uuid,
                    "socketID" to targetPlayer.socketID,
                ),
            )
        )

        val player = melodyPlayer.player ?: return
        if (preEndCallEvent.canSendMessage) {
            preEndCallEvent.canSendMessage = true
            player.sendMessage("<prefix>You have End Call Between Yourself and <count_color>${targetPlayer.name}<text>.".toComponent())
            targetPlayer.player?.sendMessage("<prefix><count_color>${melodyPlayer.name} <text>has End Call Between Themselves and You.".toComponent())
        }
        Bukkit.getServer().pluginManager.callEvent(PostEndCallEvent(melodyPlayer, targetPlayer))
    }

    fun endPendingCall(melodyPlayer: MelodyPlayer, targetPlayer: MelodyPlayer) {

        val prePendingCallEndEvent = PreEndPendingCallEvent(melodyPlayer, targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(prePendingCallEndEvent)
        if (prePendingCallEndEvent.isCancelled) return

        targetPlayer.isCallPending = false
        targetPlayer.callPendingTarget = null
        targetPlayer.isStartCall = false

        melodyPlayer.isCallPending = false
        melodyPlayer.callPendingTarget = null
        melodyPlayer.isStartCall = false

        melodyPlayer.pendingTask?.cancel()
        targetPlayer.pendingTask?.cancel()

        melodyPlayer.pendingTask = null
        targetPlayer.pendingTask = null

        Websocket.socket.emit(
            "onPendingCallEndPlugin",
            mapOf(
                "player" to mapOf(
                    "name" to targetPlayer.name,
                    "uuid" to targetPlayer.uuid,
                    "socketID" to melodyPlayer.socketID,
                ),
                "target" to mapOf(
                    "name" to melodyPlayer.name,
                    "uuid" to melodyPlayer.uuid,
                    "socketID" to targetPlayer.socketID,
                ),
            )
        )
        if (prePendingCallEndEvent.canSendMessage) {
            prePendingCallEndEvent.canSendMessage = true
            melodyPlayer.player?.sendMessage("<prefix><count_color>${targetPlayer.name} <text>is not Available Please try Again later.".toComponent())
            targetPlayer.player?.sendMessage("<prefix>Pending Call has End.".toComponent())
        }

        Bukkit.getServer().pluginManager.callEvent(PostPendingCallEndEvent(melodyPlayer, targetPlayer))
    }

    fun acceptCall(melodyPlayer: MelodyPlayer, targetPlayer: MelodyPlayer) {

        val preAcceptChangeServerEvent = PreAcceptCallEvent(melodyPlayer, targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(preAcceptChangeServerEvent)
        if (preAcceptChangeServerEvent.isCancelled) return

        melodyPlayer.isCallPending = false
        melodyPlayer.callPendingTarget = null

        targetPlayer.isCallPending = false
        targetPlayer.callPendingTarget = null

        melodyPlayer.isInCall = true
        melodyPlayer.callTarget = targetPlayer

        targetPlayer.isInCall = true
        targetPlayer.callTarget = melodyPlayer

        melodyPlayer.pendingTask?.cancel()
        targetPlayer.pendingTask?.cancel()

        melodyPlayer.pendingTask = null
        targetPlayer.pendingTask = null

        Websocket.socket.emit(
            "onAcceptCallPlugin",
            mapOf(
                "player" to mapOf(
                    "name" to targetPlayer.name,
                    "uuid" to targetPlayer.uuid,
                    "socketID" to melodyPlayer.socketID,
                ),
                "target" to mapOf(
                    "name" to melodyPlayer.name,
                    "uuid" to melodyPlayer.uuid,
                    "socketID" to targetPlayer.socketID,
                ),
            )
        )
        if (preAcceptChangeServerEvent.canSendMessage) {
            preAcceptChangeServerEvent.canSendMessage = true
            val player = melodyPlayer.player ?: return
            player.sendMessage("<prefix>Call has been Started with <count_color>${targetPlayer.name}<text>, You can use <i>/melodymine call end</i> to end the Call.".toComponent())
            targetPlayer.player?.sendMessage("<prefix>Call has Started with <count_color>${melodyPlayer.name}<text>, You can use <i>/melodymine call end</i> to end the Call.".toComponent())
        }

        Bukkit.getServer().pluginManager.callEvent(PostAcceptCallEvent(melodyPlayer, targetPlayer))
    }

    fun denyCall(melodyPlayer: MelodyPlayer, targetPlayer: MelodyPlayer) {

        val preDenyCallEvent = PreDenyCallEvent(melodyPlayer, targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(preDenyCallEvent)
        if (preDenyCallEvent.isCancelled) return

        melodyPlayer.isCallPending = false
        melodyPlayer.callPendingTarget = null
        melodyPlayer.isStartCall = false

        targetPlayer.isCallPending = false
        targetPlayer.callPendingTarget = null
        targetPlayer.isStartCall = false

        melodyPlayer.pendingTask?.cancel()
        targetPlayer.pendingTask?.cancel()

        melodyPlayer.pendingTask = null
        targetPlayer.pendingTask = null

        Websocket.socket.emit(
            "onDenyCallPlugin",
            mapOf(
                "player" to mapOf(
                    "name" to targetPlayer.name,
                    "uuid" to targetPlayer.uuid,
                    "socketID" to melodyPlayer.socketID,
                ),
                "target" to mapOf(
                    "name" to melodyPlayer.name,
                    "uuid" to melodyPlayer.uuid,
                    "socketID" to targetPlayer.socketID,
                ),
            )
        )
        if (preDenyCallEvent.canSendMessage) {
            preDenyCallEvent.canSendMessage = true
            val player = melodyPlayer.player ?: return
            player.sendMessage("<prefix>Call Request has been Deny.".toComponent())
            targetPlayer.player?.sendMessage("<prefix><count_color>${player.name}<text>, Deny Call Request.".toComponent())
        }

        Bukkit.getServer().pluginManager.callEvent(PostDenyCallEvent(melodyPlayer, targetPlayer))
    }

    fun toggleCall(melodyPlayer: MelodyPlayer) {
        val player = melodyPlayer.player ?: return
        val preToggleCallEvent = PreToggleCallEvent(melodyPlayer)
        Bukkit.getServer().pluginManager.callEvent(preToggleCallEvent)
        if (preToggleCallEvent.isCancelled) return
        if (preToggleCallEvent.canSendMessage) {
            preToggleCallEvent.canSendMessage = true
            if (!melodyPlayer.callToggle) {
                player.sendMessage("<prefix>Your Call Requests has been Disabled, Now Players can not Send Call Request.".toComponent())
            } else {
                player.sendMessage("<prefix>Your Call Requests has been Enabled, Now Players can Send Call Request.".toComponent())
            }
        }

        melodyPlayer.callToggle = !melodyPlayer.callToggle

        Bukkit.getServer().pluginManager.callEvent(PostToggleCallEvent(melodyPlayer))
    }

    fun playSound(soundName: String, sendToAll: Boolean = false, socketID: String?) {
        val prePlaySoundEvent = PrePlaySoundEvent(soundName, sendToAll, socketID)
        Bukkit.getServer().pluginManager.callEvent(prePlaySoundEvent)
        if (prePlaySoundEvent.isCancelled) return
        Websocket.socket.emit(
            "onPlaySoundPlugin",
            mapOf(
                "socketID" to socketID!!,
                "soundName" to soundName,
                "sendToAll" to sendToAll
            )
        )
        Bukkit.getServer().pluginManager.callEvent(PostPlaySoundEvent(soundName, sendToAll, socketID))
    }

    fun pauseSound(soundName: String, sendToAll: Boolean = false, socketID: String?) {
        val prePauseSoundEvent = PrePauseSoundEvent(soundName, sendToAll, socketID)
        Bukkit.getServer().pluginManager.callEvent(prePauseSoundEvent)
        if (prePauseSoundEvent.isCancelled) return
        Websocket.socket.emit(
            "onPauseSoundPlugin",
            mapOf(
                "socketID" to socketID!!,
                "soundName" to soundName,
                "sendTOAll" to sendToAll
            )
        )
        Bukkit.getServer().pluginManager.callEvent(PrePauseSoundEvent(soundName, sendToAll, socketID))
    }

    fun stopSound(soundName: String, sendToAll: Boolean = false, socketID: String?) {
        val preStopSoundEvent = PreStopSoundEvent(soundName, sendToAll, socketID)
        Bukkit.getServer().pluginManager.callEvent(preStopSoundEvent)
        if (preStopSoundEvent.isCancelled) return
        Websocket.socket.emit(
            "onStopSoundPlugin",
            mapOf(
                "socketID" to socketID!!,
                "soundName" to soundName,
                "sendTOAll" to sendToAll
            )
        )
        Bukkit.getServer().pluginManager.callEvent(PostStopSoundEvent(soundName, sendToAll, socketID))
    }

}