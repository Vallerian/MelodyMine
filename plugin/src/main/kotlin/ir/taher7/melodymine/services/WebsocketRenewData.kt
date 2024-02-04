package ir.taher7.melodymine.services

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.storage.Storage
import org.bukkit.scheduler.BukkitRunnable

class WebsocketRenewData {
    init {
        start()
    }

    private fun start() {
        object : BukkitRunnable() {
            override fun run() {
                if (Storage.onlinePlayers.isNotEmpty() && Websocket.socket.isActive) {
                    Storage.onlinePlayers.values.filter { player -> player.webIsOnline && player.isActiveVoice && !player.adminMode }
                        .forEach { melodyPlayer ->
                            Storage.onlinePlayers.values.filter { player -> player.uuid != melodyPlayer.uuid && player.webIsOnline && player.isActiveVoice && !player.adminMode && player.callTarget != melodyPlayer }
                                .forEach { targetPlayer ->
                                    val playerLocation = melodyPlayer.player?.location
                                    val targetLocation = targetPlayer.player?.location

                                    if (playerLocation != null &&
                                        targetLocation != null &&
                                        playerLocation.world == targetLocation.world
                                    ) {
                                        val distance = playerLocation.distance(targetLocation)
                                        val maxDistance = Storage.maxDistance

                                        if (distance < (maxDistance + 50)) {
                                            if (!melodyPlayer.isSendOffer.contains(targetPlayer.uuid)) {
                                                melodyPlayer.isSendOffer.add(targetPlayer.uuid)
                                                if (!targetPlayer.isSendOffer.contains(melodyPlayer.uuid)) {
                                                    targetPlayer.socketID?.let {
                                                        object : BukkitRunnable() {
                                                            override fun run() {
                                                                MelodyManager.enableVoice(
                                                                    melodyPlayer.name,
                                                                    melodyPlayer.uuid,
                                                                    melodyPlayer.server,
                                                                    it
                                                                )
                                                            }
                                                        }.runTaskLater(MelodyMine.instance,5)
                                                    }
                                                }
                                            }
                                        }

                                        if (distance < (maxDistance + 100)) {
                                            val targetSocketID = targetPlayer.socketID
                                            if (targetSocketID != null) {
                                                object : BukkitRunnable() {
                                                    override fun run() {
                                                        melodyPlayer.player?.eyeLocation?.let {
                                                            targetPlayer.player?.eyeLocation?.let { it1 ->
                                                                MelodyManager.setVolume(
                                                                    melodyPlayer.uuid,
                                                                    targetSocketID,
                                                                    it,
                                                                    it1
                                                                )
                                                            }
                                                        }
                                                    }
                                                }.runTask(MelodyMine.instance)
                                            }
                                        }


                                        if (distance > (maxDistance + 150)) {
                                            if (melodyPlayer.isSendOffer.contains(targetPlayer.uuid)) {
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
                                                        }
                                                    }.runTask(MelodyMine.instance)
                                                }
                                            }
                                        }


                                    }
                                }

                        }
                }
            }
        }.runTaskTimerAsynchronously(MelodyMine.instance, 0L, Storage.updateDistanceTime)
    }
}