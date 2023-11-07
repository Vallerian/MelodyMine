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
                if (Storage.onlinePlayers.isNotEmpty()) {
                    Storage.onlinePlayers.values.filter { player -> player.webIsOnline && player.isActiveVoice && !player.adminMode }
                        .forEach { melodyPlayer ->
                            Storage.onlinePlayers.values.filter { player -> player.uuid != melodyPlayer.uuid && player.webIsOnline && player.isActiveVoice && !player.adminMode && player.callTarget != melodyPlayer}
                                .forEach { targetPlayer ->
                                    val playerLocation = melodyPlayer.player?.location
                                    val targetLocation = targetPlayer.player?.location

                                    if (playerLocation != null &&
                                        targetLocation != null &&
                                        playerLocation.world == targetLocation.world
                                    ) {
                                        val distance = playerLocation.distance(targetLocation)
                                        var volume: Double
                                        val hearDistance = Storage.hearDistance
                                        if (distance < hearDistance) {
                                            if (!melodyPlayer.isSendOffer.contains(targetPlayer.uuid)) {
                                                melodyPlayer.isSendOffer.add(targetPlayer.uuid)
                                                if (!targetPlayer.isSendOffer.contains(melodyPlayer.uuid)) {
                                                    melodyPlayer.socketID?.let {
                                                        object : BukkitRunnable() {
                                                            override fun run() {
                                                                MelodyManager.enableVoice(
                                                                    targetPlayer.name,
                                                                    targetPlayer.uuid,
                                                                    targetPlayer.server,
                                                                    it
                                                                )
                                                            }
                                                        }.runTask(MelodyMine.instance)
                                                    }
                                                }
                                            }

                                            volume = if (Storage.hearLazy) {
                                                (hearDistance - distance) / hearDistance
                                            } else {
                                                1.0
                                            }
                                            val targetSocketID = targetPlayer.socketID
                                            if (targetSocketID != null) {
                                                if (melodyPlayer.adminMode) {
                                                    volume = 1.0
                                                }

                                                if (melodyPlayer.isMute) {
                                                    volume = 0.0
                                                }

                                                object : BukkitRunnable() {
                                                    override fun run() {
                                                        MelodyManager.setVolume(
                                                            melodyPlayer.uuid,
                                                            volume,
                                                            targetSocketID,
                                                        )
                                                    }
                                                }.runTask(MelodyMine.instance)
                                            }
                                        } else {
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
                                                        }
                                                    }.runTask(MelodyMine.instance)
                                                    melodyPlayer.isSendOffer.remove(targetPlayer.uuid)
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