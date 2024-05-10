package ir.taher7.melodymine.services

import com.google.common.util.concurrent.ThreadFactoryBuilder
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.models.RenewData
import ir.taher7.melodymine.models.RenewPlayer
import ir.taher7.melodymine.storage.Settings
import ir.taher7.melodymine.storage.Storage
import org.bukkit.scheduler.BukkitRunnable
import java.text.DecimalFormat
import java.util.concurrent.Executors


class WebsocketRenewData {
    init {
        startConnectionTrack()
    }

    val decimalFormat = DecimalFormat("#0.0")
    val threadFactory =
        ThreadFactoryBuilder()
            .setNameFormat("${MelodyMine.instance.description.name.lowercase()}-renew-data-thread-%d")
            .build()

    val connectionThreadPool = Executors.newFixedThreadPool(10, threadFactory)


    private fun startConnectionTrack() {

        object : BukkitRunnable() {
            override fun run() {

                connectionThreadPool.submit {
                    if (Storage.onlinePlayers.isNotEmpty() && Websocket.socket.isActive) {
                        val players: MutableList<RenewPlayer> = mutableListOf()
                        val connect: MutableList<List<Int>> = mutableListOf()
                        val disconnect: MutableList<List<Int>> = mutableListOf()
                        val volume: MutableList<List<Int>> = mutableListOf()

                        Storage.onlinePlayers.values.filter { player -> player.webIsOnline && player.isActiveVoice && !player.adminMode }
                            .forEach { melodyPlayer ->
                                Storage.onlinePlayers.values.filter { player -> player.uuid != melodyPlayer.uuid && player.webIsOnline && player.isActiveVoice && !player.adminMode && player.callTarget != melodyPlayer }
                                    .forEach { targetPlayer ->
                                        val playerLocation = melodyPlayer.player?.location
                                        val targetLocation = targetPlayer.player?.location

                                        if (Settings.disableWorlds.contains(playerLocation?.world?.name) ||
                                            Settings.disableWorlds.contains(targetLocation?.world?.name)
                                        ) return@forEach

                                        if (playerLocation != null &&
                                            targetLocation != null &&
                                            playerLocation.world == targetLocation.world
                                        ) {
                                            val distance = playerLocation.distance(targetLocation)

                                            if (distance < (Settings.renewConnection)) {
                                                if (!melodyPlayer.isSendOffer.contains(targetPlayer.uuid)) {
                                                    melodyPlayer.isSendOffer.add(targetPlayer.uuid)

                                                    if (!targetPlayer.isSendOffer.contains(melodyPlayer.uuid)) {
                                                        val renewPlayer = createRenewPlayer(melodyPlayer)
                                                        if (!players.contains(renewPlayer)) players.add(renewPlayer)

                                                        val renewTarget = createRenewPlayer(targetPlayer)
                                                        if (!players.contains(renewTarget)) players.add(renewTarget)

                                                        connect.add(
                                                            listOf(
                                                                players.indexOf(renewPlayer),
                                                                players.indexOf(renewTarget)
                                                            )
                                                        )

                                                    }
                                                }
                                            }


                                            if (distance < (Settings.renewVolume)) {

                                                val renewPlayer = createRenewPlayer(melodyPlayer)
                                                if (!players.contains(renewPlayer)) players.add(renewPlayer)

                                                val renewTarget = createRenewPlayer(targetPlayer)
                                                if (!players.contains(renewTarget)) players.add(renewTarget)

                                                volume.add(
                                                    listOf(
                                                        players.indexOf(renewPlayer),
                                                        players.indexOf(renewTarget)
                                                    )
                                                )
                                            }


                                            if (distance > (Settings.renewDisconnect)) {
                                                if (melodyPlayer.isSendOffer.contains(targetPlayer.uuid)) {
                                                    val renewPlayer = createRenewPlayer(melodyPlayer)
                                                    if (!players.contains(renewPlayer)) players.add(renewPlayer)

                                                    val renewTarget = createRenewPlayer(targetPlayer)
                                                    if (!players.contains(renewTarget)) players.add(renewTarget)

                                                    disconnect.add(
                                                        listOf(
                                                            players.indexOf(renewPlayer),
                                                            players.indexOf(renewTarget)
                                                        )
                                                    )
                                                    melodyPlayer.isSendOffer.remove(targetPlayer.uuid)
                                                }
                                            }
                                        }
                                    }
                            }
                        if (connect.isNotEmpty() || disconnect.isNotEmpty() || volume.isNotEmpty())
                            MelodyManager.renewData(
                                RenewData(
                                    players,
                                    connect.ifEmpty { null },
                                    disconnect.ifEmpty { null },
                                    volume.ifEmpty { null }
                                )
                            )

                    }
                }
            }
        }.runTaskTimer(MelodyMine.instance, 0L, Settings.renewInterval)
    }

    private fun createRenewPlayer(melodyPlayer: MelodyPlayer): RenewPlayer {


        return RenewPlayer(
            id = melodyPlayer.id,
            l = listOf(
                melodyPlayer.player?.location?.x?.toInt()!!,
                melodyPlayer.player?.location?.y?.toInt()!!,
                melodyPlayer.player?.location?.z?.toInt()!!,
            ),
            d = listOf(
                decimalFormat.format(melodyPlayer.player?.location?.direction?.x).toDouble(),
                decimalFormat.format(melodyPlayer.player?.location?.direction?.y).toDouble(),
                decimalFormat.format(melodyPlayer.player?.location?.direction?.z).toDouble(),
            )
        )
    }


}