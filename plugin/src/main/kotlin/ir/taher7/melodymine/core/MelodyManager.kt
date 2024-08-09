package ir.taher7.melodymine.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.api.events.*
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.models.PlayerStatus
import ir.taher7.melodymine.models.RenewData
import ir.taher7.melodymine.models.SoundSettings
import ir.taher7.melodymine.services.Websocket
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Settings
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.storage.Talk
import ir.taher7.melodymine.utils.Adventure.sendComponent
import ir.taher7.melodymine.utils.QRCodeRenderer
import ir.taher7.melodymine.utils.Utils
import net.glxn.qrgen.javase.QRCode
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable


object MelodyManager {
    private var gson: Gson

    init {
        val builder = GsonBuilder()
        builder.excludeFieldsWithoutExposeAnnotation()
        gson = builder.create()
    }


    fun mute(uuid: String) {
        val targetPlayer = Storage.onlinePlayers[uuid] ?: return

        val prePlayerMuteEvent = PrePlayerMuteEvent(targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(prePlayerMuteEvent)
        if (prePlayerMuteEvent.isCancelled) return

        if (targetPlayer.isActiveVoice) {
            targetPlayer.talkBossBar?.setBossBarServerMute()
            targetPlayer.talkNameTag?.setNameTagServerMute()
        }

        targetPlayer.isMute = true
        Database.updatePlayer(targetPlayer, false)
        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onPlayerMutePlugin", mapOf(
                        "uuid" to targetPlayer.uuid
                    )
                )
            }
        }.runTaskAsynchronously(MelodyMine.instance)

        Bukkit.getServer().pluginManager.callEvent(PostPlayerMuteEvent(targetPlayer))
    }

    fun unMute(uuid: String) {
        val targetPlayer = Storage.onlinePlayers[uuid] ?: return

        val prePlayerUnMuteEvent = PreUnMutePlayerEvent(targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(prePlayerUnMuteEvent)
        if (prePlayerUnMuteEvent.isCancelled) return

        targetPlayer.talkBossBar?.setBossBarInactive()
        targetPlayer.talkNameTag?.setNameTagInactive()

        targetPlayer.isMute = false
        Database.updatePlayer(targetPlayer, false)
        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onPlayerUnmutePlugin", mapOf(
                        "uuid" to targetPlayer.uuid
                    )
                )
            }
        }.runTaskAsynchronously(MelodyMine.instance)

        Bukkit.getServer().pluginManager.callEvent(PostUnMutePlayerEvent(targetPlayer))
    }

    fun sendStartLink(player: Player) {
        Database.getVerifyCode(player) { result ->
            player.sendComponent(
                "<click:open_url:'${Utils.clientURL()}/login?verifyCode=${result}${if (Settings.autoStart) "&start=true" else ""}'>${
                    Messages.getMessage("website.open")
                }</click>"
            )
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
                                QRCode
                                    .from("${Utils.clientURL()}/login?verifyCode=${result}${if (Settings.autoStart) "&start=true" else ""}")
                                    .file()
                            )
                        )

                        val map = Utils.createQRCodeMap(view)
                        if (offhand) {
                            player.inventory.setItemInOffHand(map)
                        } else {
                            player.inventory.setItem(slot, map)
                            player.inventory.heldItemSlot = slot
                        }

                        player.sendComponent(Messages.getMessage("commands.start.scan_qrcode"))
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
        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onAdminModeEnablePlugin", mapOf(
                        "uuid" to uuid,
                        "server" to Settings.server
                    )
                )
            }
        }.runTaskAsynchronously(MelodyMine.instance)


        Bukkit.getServer().pluginManager.callEvent(PostEnableAdminMode(targetPlayer))
    }

    fun disableAdminMode(uuid: String) {
        val targetPlayer = Storage.onlinePlayers[uuid] ?: return

        val preDisableAdminModeEvent = PreDisableAdminModeEvent(targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(preDisableAdminModeEvent)
        if (preDisableAdminModeEvent.isCancelled) return

        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onAdminModeDisablePlugin", mapOf(
                        "uuid" to uuid,
                        "server" to Settings.server
                    )
                )
            }
        }.runTaskAsynchronously(MelodyMine.instance)

        targetPlayer.adminMode = false
        targetPlayer.isSendOffer = arrayListOf()
        Storage.onlinePlayers.values.forEach { item ->
            if (item.isSendOffer.contains(targetPlayer.uuid)) {
                item.isSendOffer.remove(targetPlayer.uuid)
            }
        }
        Bukkit.getServer().pluginManager.callEvent(PostDisableAdminMode(targetPlayer))
    }

    fun toggleLogger(uuid: String) {
        val targetPlayer = Storage.onlinePlayers[uuid] ?: return
        targetPlayer.isToggle = !targetPlayer.isToggle
    }

    fun enableVoice(playerName: String, playerUuid: String, playerServer: String, targetSocketID: String) {
        val preEnableVoiceEvent = PreEnableVoiceEvent(playerName, playerUuid, playerServer, targetSocketID)
        Bukkit.getServer().pluginManager.callEvent(preEnableVoiceEvent)
        if (preEnableVoiceEvent.isCancelled) return

        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onEnableVoicePlugin", mapOf(
                        "uuid" to playerUuid,
                        "server" to playerServer,
                        "socketID" to targetSocketID
                    )
                )
            }
        }.runTaskAsynchronously(MelodyMine.instance)

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

        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onDisableVoicePlugin", mapOf(
                        "uuid" to playerUuid,
                        "server" to playerServer,
                        "socketID" to targetSocketID
                    )
                )
            }
        }.runTaskAsynchronously(MelodyMine.instance)

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
        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onSetVolumePlugin",
                    mapOf(
                        "uuid" to playerUuid,
                        "distance" to playerLocation.distance(targetLocation),
                        "socketID" to targetSocketID,
                        "settings" to mapOf(
                            "lazyHear" to Settings.lazyHear,
                            "maxDistance" to Settings.maxDistance,
                            "refDistance" to Settings.refDistance,
                            "rolloffFactor" to Settings.rolloffFactor,
                            "innerAngle" to Settings.innerAngle,
                            "outerAngle" to Settings.outerAngle,
                            "outerVolume" to Settings.outerVolume,
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
            }
        }.runTaskAsynchronously(MelodyMine.instance)

        Bukkit.getServer().pluginManager.callEvent(PostSetVolumeEvent(playerUuid, targetSocketID))
    }

    fun setPlayerSelfMute(melodyPlayer: MelodyPlayer, value: Boolean) {
        val prePlayerSetSelfMuteEvent = PrePlayerSetSelfMuteEvent(melodyPlayer, value)
        Bukkit.getServer().pluginManager.callEvent(prePlayerSetSelfMuteEvent)
        if (prePlayerSetSelfMuteEvent.isCancelled) return
        if (!value) {
            melodyPlayer.talkBossBar?.setBossBarSelfMute()
            melodyPlayer.talkNameTag?.setNameTagSelfMute()

        } else {
            melodyPlayer.talkBossBar?.setBossBarInactive()
            melodyPlayer.talkNameTag?.setNameTagInactive()
        }
        melodyPlayer.isSelfMute = value
        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onSetControlPlugin",
                    mapOf(
                        "name" to melodyPlayer.name,
                        "uuid" to melodyPlayer.uuid,
                        "type" to "mic",
                        "server" to melodyPlayer.server,
                        "value" to prePlayerSetSelfMuteEvent.value,
                    )
                )
            }
        }.runTaskAsynchronously(MelodyMine.instance)

        Bukkit.getServer().pluginManager.callEvent(PostPlayerSetSelfMuteEvent(melodyPlayer, value))
    }

    fun setPlayerDeafen(melodyPlayer: MelodyPlayer, value: Boolean) {
        val prePlayerSetDeafenEvent = PrePlayerSetDeafenEvent(melodyPlayer, value)
        Bukkit.getServer().pluginManager.callEvent(prePlayerSetDeafenEvent)
        if (prePlayerSetDeafenEvent.isCancelled) return

        melodyPlayer.isDeafen = value
        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onSetControlPlugin",
                    mapOf(
                        "name" to melodyPlayer.name,
                        "uuid" to melodyPlayer.uuid,
                        "type" to "sound",
                        "server" to melodyPlayer.server,
                        "value" to prePlayerSetDeafenEvent.value,
                    )
                )
            }
        }.runTaskAsynchronously(MelodyMine.instance)

        Bukkit.getServer().pluginManager.callEvent(PostPlayerSetDeafenEvent(melodyPlayer, value))
    }

    fun startCall(melodyPlayer: MelodyPlayer, targetPlayer: MelodyPlayer) {

        val preStartCallEvent = PreStartCallEvent(melodyPlayer, targetPlayer)
        Bukkit.getServer().pluginManager.callEvent(preStartCallEvent)
        if (preStartCallEvent.isCancelled) return

        if (preStartCallEvent.canSendMessage) {
            preStartCallEvent.canSendMessage = true
            melodyPlayer.player?.sendComponent(Messages.getMessage("general.content_header"))
            melodyPlayer.player?.sendComponent("")
            melodyPlayer.player?.sendComponent(
                Messages.getMessage(
                    "commands.call.call_wait",
                    hashMapOf("{PLAYER}" to melodyPlayer.name)
                )
            )
            melodyPlayer.player?.sendComponent(Messages.getMessage("commands.call.accept_button"))
            melodyPlayer.player?.sendComponent("")
            melodyPlayer.player?.sendComponent(Messages.getMessage("general.content_footer"))
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
        }, Settings.callPendingTime)

        melodyPlayer.pendingTask = callTask
        targetPlayer.pendingTask = callTask

        object : BukkitRunnable() {
            override fun run() {
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
            }
        }.runTaskAsynchronously(MelodyMine.instance)

        if (preStartCallEvent.canSendMessage) {
            preStartCallEvent.canSendMessage = true
            targetPlayer.player?.sendComponent(Messages.getMessage("general.content_header"))
            targetPlayer.player?.sendComponent("")
            targetPlayer.player?.sendComponent(
                Messages.getMessage(
                    "commands.call.call_receive",
                    hashMapOf("{PLAYER}" to melodyPlayer.name)
                )
            )
            targetPlayer.player?.sendComponent(Messages.getMessage("commands.call.call_accept"))
            targetPlayer.player?.sendComponent(Messages.getMessage("commands.call.call_deny_button"))
            targetPlayer.player?.sendComponent("")
            targetPlayer.player?.sendComponent(Messages.getMessage("general.content_footer"))
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

        object : BukkitRunnable() {
            override fun run() {
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
            }
        }.runTaskAsynchronously(MelodyMine.instance)

        val player = melodyPlayer.player ?: return
        if (preEndCallEvent.canSendMessage) {
            preEndCallEvent.canSendMessage = true
            player.sendComponent(
                Messages.getMessage(
                    "commands.call.call_end_target",
                    hashMapOf("{PLAYER}" to targetPlayer.name)
                )
            )
            targetPlayer.player?.sendComponent(
                Messages.getMessage(
                    "commands.call.call_end",
                    hashMapOf("{PLAYER}" to melodyPlayer.name)
                )
            )
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

        object : BukkitRunnable() {
            override fun run() {
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
            }
        }.runTaskAsynchronously(MelodyMine.instance)


        if (prePendingCallEndEvent.canSendMessage) {
            prePendingCallEndEvent.canSendMessage = true
            melodyPlayer.player?.sendComponent(
                Messages.getMessage(
                    "commands.call.not_available",
                    hashMapOf("{PLAYER}" to targetPlayer.name)
                )
            )
            targetPlayer.player?.sendComponent(Messages.getMessage("commands.call.call_pending_end"))
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

        object : BukkitRunnable() {
            override fun run() {
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
            }
        }.runTaskAsynchronously(MelodyMine.instance)

        if (preAcceptChangeServerEvent.canSendMessage) {
            preAcceptChangeServerEvent.canSendMessage = true
            val player = melodyPlayer.player ?: return
            player.sendComponent(
                Messages.getMessage(
                    "commands.call.call_start",
                    hashMapOf("{PLAYER}" to targetPlayer.name)
                )
            )
            targetPlayer.player?.sendComponent(
                Messages.getMessage(
                    "commands.call.call_start",
                    hashMapOf("{PLAYER}" to melodyPlayer.name)
                )
            )
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


        object : BukkitRunnable() {
            override fun run() {
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
            }
        }.runTaskAsynchronously(MelodyMine.instance)
        if (preDenyCallEvent.canSendMessage) {
            preDenyCallEvent.canSendMessage = true
            val player = melodyPlayer.player ?: return
            player.sendComponent(Messages.getMessage("commands.call.call_deny"))
            targetPlayer.player?.sendComponent(
                Messages.getMessage(
                    "commands.call.call_deny_others",
                    hashMapOf("{PLAYER}" to player.name)
                )
            )
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
                player.sendComponent(Messages.getMessage("commands.call.call_request_disable"))
            } else {
                player.sendComponent(Messages.getMessage("commands.call.call_request_enable"))
            }
        }

        melodyPlayer.callToggle = !melodyPlayer.callToggle

        Bukkit.getServer().pluginManager.callEvent(PostToggleCallEvent(melodyPlayer))
    }

    fun playSound(soundName: String, sendToAll: Boolean = false, socketID: String?, volume: Double? = null) {
        val prePlaySoundEvent = PrePlaySoundEvent(soundName, sendToAll, socketID, volume)
        Bukkit.getServer().pluginManager.callEvent(prePlaySoundEvent)
        if (prePlaySoundEvent.isCancelled) return

        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onPlaySoundPlugin",
                    mapOf(
                        "socketID" to socketID!!,
                        "soundName" to soundName,
                        "sendToAll" to sendToAll,
                        "volume" to volume,
                    )
                )
            }
        }.runTaskAsynchronously(MelodyMine.instance)
        Bukkit.getServer().pluginManager.callEvent(PostPlaySoundEvent(soundName, sendToAll, socketID, volume))
    }

    fun pauseSound(soundName: String, sendToAll: Boolean = false, socketID: String?) {
        val prePauseSoundEvent = PrePauseSoundEvent(soundName, sendToAll, socketID)
        Bukkit.getServer().pluginManager.callEvent(prePauseSoundEvent)
        if (prePauseSoundEvent.isCancelled) return
        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onPauseSoundPlugin",
                    mapOf(
                        "socketID" to socketID!!,
                        "soundName" to soundName,
                        "sendTOAll" to sendToAll
                    )
                )
            }
        }.runTaskAsynchronously(MelodyMine.instance)
        Bukkit.getServer().pluginManager.callEvent(PrePauseSoundEvent(soundName, sendToAll, socketID))
    }

    fun stopSound(soundName: String, sendToAll: Boolean = false, socketID: String?) {
        val preStopSoundEvent = PreStopSoundEvent(soundName, sendToAll, socketID)
        Bukkit.getServer().pluginManager.callEvent(preStopSoundEvent)
        if (preStopSoundEvent.isCancelled) return
        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onStopSoundPlugin",
                    mapOf(
                        "socketID" to socketID!!,
                        "soundName" to soundName,
                        "sendTOAll" to sendToAll
                    )
                )
            }
        }.runTaskAsynchronously(MelodyMine.instance)

        Bukkit.getServer().pluginManager.callEvent(PostStopSoundEvent(soundName, sendToAll, socketID))
    }

    fun changeSoundVolume(soundName: String, sendToAll: Boolean = false, socketID: String?, volume: Double) {
        val preChangeSoundVolumeEvent = PreChangeSoundVolumeEvent(soundName, sendToAll, socketID, volume)
        Bukkit.getServer().pluginManager.callEvent(preChangeSoundVolumeEvent)
        if (preChangeSoundVolumeEvent.isCancelled) return

        object : BukkitRunnable() {
            override fun run() {
                Websocket.socket.emit(
                    "onVolumeSoundPlugin",
                    mapOf(
                        "socketID" to socketID!!,
                        "soundName" to soundName,
                        "sendTOAll" to sendToAll,
                        "volume" to volume,
                    )
                )
            }
        }.runTaskAsynchronously(MelodyMine.instance)
        Bukkit.getServer().pluginManager.callEvent(PostChangeSoundVolumeEvent(soundName, sendToAll, socketID, volume))
    }


    fun showPlayerIsTalking(player: MelodyPlayer) {
        if (Talk.isEnableBossBar) {
            if (player.isMute) {
                player.talkBossBar?.setBossBarServerMute()
            } else {
                if (!player.isSelfMute) {
                    player.talkBossBar?.setBossBarSelfMute()
                } else {
                    if (player.isTalk) {
                        player.talkBossBar?.setBossBarActive()
                    } else {
                        player.talkBossBar?.setBossBarInactive()
                    }
                }
            }
        }

        if (Talk.isEnableNameTag) {
            if (player.isMute) {
                player.talkNameTag?.setNameTagServerMute()
            } else {
                if (!player.isSelfMute) {
                    player.talkNameTag?.setNameTagSelfMute()
                } else {
                    if (player.isTalk) {
                        player.talkNameTag?.setNameTagActive()
                    } else {
                        player.talkNameTag?.setNameTagInactive()
                    }
                }
            }
        }
    }


    fun renewData(data: RenewData) {
        val preRenewDataEvent = PreRenewDataEvent(data)
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.getServer().pluginManager.callEvent(preRenewDataEvent)
            }
        }.runTask(MelodyMine.instance)

        if (preRenewDataEvent.isCancelled) return

        Websocket.socket.emit(
            "onRenewData", gson.toJson(data)
        )
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.getServer().pluginManager.callEvent(PostRenewDataEvent(data))
            }
        }.runTask(MelodyMine.instance)
    }

    fun sendSoundSetting(socketID: String) {

        val preSoundSettingsEvent = PreSendSoundSettingsEvent(socketID)
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.getServer().pluginManager.callEvent(preSoundSettingsEvent)
            }
        }.runTask(MelodyMine.instance)

        if (preSoundSettingsEvent.isCancelled) return

        Websocket.socket.emit(
            "onSoundSettings", mapOf(
                "socketID" to socketID,
                "soundSettings" to gson.toJson(
                    SoundSettings(
                        lazyHear = Settings.lazyHear,
                        maxDistance = Settings.maxDistance,
                        refDistance = Settings.refDistance,
                        rolloffFactor = Settings.rolloffFactor,
                        innerAngle = Settings.innerAngle,
                        outerAngle = Settings.outerAngle,
                        outerVolume = Settings.outerVolume
                    )
                ),
                "playerStatus" to gson.toJson(Storage.onlinePlayers.values.filter { player ->
                    player.isActiveVoice && player.socketID != socketID
                }.map { melodyPlayer ->
                    PlayerStatus(
                        uuid = melodyPlayer.uuid,
                        isMute = melodyPlayer.isSelfMute,
                        isDeafen = melodyPlayer.isDeafen
                    )
                })
            )
        )


        object : BukkitRunnable() {
            override fun run() {
                Bukkit.getServer().pluginManager.callEvent(PostSendSoundSettingsEvent(socketID))
            }
        }.runTask(MelodyMine.instance)
    }

    fun checkPlayerWebConnection(player: MelodyPlayer) {
        if (!player.webIsOnline) return
        Websocket.socket.emit(
            "onCheckPlayer",
            mapOf(
                "socketID" to player.socketID,
                "uuid" to player.uuid
            )
        )
    }

    fun getMelodyPlayer(uuid: String): MelodyPlayer? {
        return Storage.onlinePlayers[uuid]
    }

    fun getMelodyPlayerFromSocketID(socketID: String): MelodyPlayer? {
        return Storage.onlinePlayers.values.find { player -> player.socketID == socketID }
    }
}