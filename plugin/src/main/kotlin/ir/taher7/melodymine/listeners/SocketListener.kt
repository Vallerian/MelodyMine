package ir.taher7.melodymine.listeners

import com.google.gson.GsonBuilder
import io.socket.client.Socket
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.api.events.*
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.AdventureUtils.sendActionbar
import ir.taher7.melodymine.utils.AdventureUtils.sendMessage
import ir.taher7.melodymine.utils.AdventureUtils.toComponent
import ir.taher7.melodymine.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.io.IOException


class SocketListener(private val socket: Socket) {

    init {
        initListeners()
    }

    private fun initListeners() {
        val builder = GsonBuilder()
        builder.excludeFieldsWithoutExposeAnnotation()
        val gson = builder.create()

        socket.on("onPlayerJoinToWeb") { args ->
            val melodyPlayer = gson.fromJson(args[0].toString(), MelodyPlayer::class.java)

            updateMelodyPlayer(melodyPlayer)
            Storage.onlinePlayers[melodyPlayer.uuid]?.isSendOffer = arrayListOf()
            Storage.onlinePlayers.values.forEach { player ->
                if (player.isSendOffer.contains(melodyPlayer.uuid)) {
                    player.isSendOffer.remove(melodyPlayer.uuid)
                }
            }

            Utils.sendMessageLog("<prefix>${Storage.websiteJoinLogger}", melodyPlayer)
            val player = Storage.onlinePlayers[melodyPlayer.uuid]?.player
            if (player != null) {
                when (Storage.joinWebsiteType) {
                    "message" -> {
                        player.sendMessage("<prefix>${Storage.joinWebsiteMessage}".toComponent())
                    }

                    "actionbar" -> {
                        player.sendActionbar("<text>${Storage.joinWebsiteMessage}".toComponent())
                    }

                    else -> {}
                }
            }
            object : BukkitRunnable() {
                override fun run() {
                    Bukkit.getServer().pluginManager.callEvent(PlayerJoinWebEvent(Storage.onlinePlayers[melodyPlayer.uuid]!!))
                }
            }.runTask(MelodyMine.instance)
        }

        socket.on("onNewPlayerLeaveWeb") { args ->
            val melodyPlayer = gson.fromJson(args[0].toString(), MelodyPlayer::class.java)
            updateMelodyPlayer(melodyPlayer)

            Storage.onlinePlayers[melodyPlayer.uuid]?.let { Utils.forceVoice(it) }
            Storage.onlinePlayers[melodyPlayer.uuid]?.isSendOffer = arrayListOf()
            Storage.onlinePlayers.values.forEach { player ->
                if (player.isSendOffer.contains(melodyPlayer.uuid)) {
                    player.isSendOffer.remove(melodyPlayer.uuid)
                }
            }
            Utils.sendMessageLog("<prefix>${Storage.websiteLeaveLogger}", melodyPlayer)
            val player = Storage.onlinePlayers[melodyPlayer.uuid]?.player
            if (player != null) {
                when (Storage.leaveWebsiteType) {
                    "message" -> {
                        player.sendMessage("<prefix>${Storage.leaveWebsiteMessage}".toComponent())
                    }

                    "actionbar" -> {
                        player.sendActionbar("<text>${Storage.leaveWebsiteMessage}".toComponent())
                    }

                    else -> {}
                }
            }
            object : BukkitRunnable() {
                override fun run() {
                    Bukkit.getServer().pluginManager.callEvent(PlayerLeaveWebEvent(Storage.onlinePlayers[melodyPlayer.uuid]!!))
                }
            }.runTask(MelodyMine.instance)
        }

        socket.on("onPlayerStartVoiceWeb") { args ->
            val melodyPlayer = gson.fromJson(args[0].toString(), MelodyPlayer::class.java)
            updateMelodyPlayer(melodyPlayer)
            Storage.onlinePlayers[melodyPlayer.uuid]?.isSendOffer = arrayListOf()
            Storage.onlinePlayers[melodyPlayer.uuid]?.adminMode = false
            sendPlayerData(melodyPlayer)
            Storage.onlinePlayers.values.forEach { player ->
                if (player.uuid != melodyPlayer.uuid && player.adminMode) {
                    socket.emit(
                        "onPlayerInitAdminModePlugin", mapOf(
                            "name" to player.name,
                            "uuid" to player.uuid,
                            "server" to player.server,
                            "socketID" to melodyPlayer.socketID
                        )
                    )
                }
            }
            Utils.sendMessageLog("<prefix>${Storage.websiteStartVoiceLogger}", melodyPlayer)
            val player = Storage.onlinePlayers[melodyPlayer.uuid]?.player
            if (player != null) {
                when (Storage.startVoiceType) {
                    "message" -> {
                        player.sendMessage("<prefix>${Storage.startVoiceMessage}".toComponent())
                    }

                    "actionbar" -> {
                        player.sendActionbar("<text>${Storage.startVoiceMessage}".toComponent())
                    }

                    else -> {}
                }
            }
            object : BukkitRunnable() {
                override fun run() {
                    Bukkit.getServer().pluginManager.callEvent(PlayerStartVoiceEvent(Storage.onlinePlayers[melodyPlayer.uuid]!!))
                }
            }.runTask(MelodyMine.instance)
        }

        socket.on("onPlayerEndVoiceWeb") { args ->
            val melodyPlayer = gson.fromJson(args[0].toString(), MelodyPlayer::class.java)
            Storage.onlinePlayers[melodyPlayer.uuid]?.isSendOffer = arrayListOf()
            Storage.onlinePlayers.values.forEach { player ->
                if (player.isSendOffer.contains(melodyPlayer.uuid)) {
                    player.isSendOffer.remove(melodyPlayer.uuid)
                }
            }
            updateMelodyPlayer(melodyPlayer)
            Storage.onlinePlayers[melodyPlayer.uuid]?.adminMode = false
            Utils.sendMessageLog("<prefix>${Storage.websiteEndVoiceLogger}", melodyPlayer)
            Storage.onlinePlayers[melodyPlayer.uuid]?.let { Utils.forceVoice(it) }
            val player = Storage.onlinePlayers[melodyPlayer.uuid]?.player
            if (player != null) {
                when (Storage.leaveEndType) {
                    "message" -> {
                        player.sendMessage("<prefix>${Storage.endVoiceMessage}".toComponent())
                    }

                    "actionbar" -> {
                        player.sendActionbar("<text>${Storage.endVoiceMessage}".toComponent())
                    }

                    else -> {}
                }
            }
            object : BukkitRunnable() {
                override fun run() {
                    Bukkit.getServer().pluginManager.callEvent(PlayerEndVoiceEvent(Storage.onlinePlayers[melodyPlayer.uuid]!!))
                }
            }.runTask(MelodyMine.instance)
        }

        socket.on("onPlayerChangeServerToWeb") { args ->
            val melodyPlayer = gson.fromJson(args[0].toString(), MelodyPlayer::class.java)
            if (melodyPlayer.server == Storage.server) {
                updateMelodyPlayer(melodyPlayer)
                Storage.onlinePlayers[melodyPlayer.uuid]?.isSendOffer = arrayListOf()
                Storage.onlinePlayers.values.forEach { player ->
                    if (player.isSendOffer.contains(melodyPlayer.uuid)) {
                        player.isSendOffer.remove(melodyPlayer.uuid)
                    }
                }
                sendPlayerData(melodyPlayer)
                Utils.sendMessageLog("<prefix>${melodyPlayer.name} is active voice.", melodyPlayer)
            } else {
                if (Storage.onlinePlayers.containsKey(melodyPlayer.uuid)) {
                    Storage.onlinePlayers.remove(melodyPlayer.uuid)
                }
            }
            object : BukkitRunnable() {
                override fun run() {
                    Bukkit.getServer().pluginManager.callEvent(PlayerChangeServerEvent(melodyPlayer))
                }
            }.runTask(MelodyMine.instance)
        }
    }

    private fun updateMelodyPlayer(melodyPlayer: MelodyPlayer) {
        if (melodyPlayer.server == Storage.server) {
            Storage.onlinePlayers[melodyPlayer.uuid]?.updateWebData(melodyPlayer)
        }
    }

    private fun sendPlayerData(melodyPlayer: MelodyPlayer) {
        if (melodyPlayer.server == Storage.server) {
            object : BukkitRunnable() {
                override fun run() {
                    try {
                        val player = Storage.onlinePlayers[melodyPlayer.uuid]
                        if (player == null) {
                            cancel()
                            return
                        }

                        if (!player.isActiveVoice || !player.webIsOnline) cancel()

                        Storage.onlinePlayers.values.forEach { result: MelodyPlayer ->
                            if (result.uuid != player.uuid && result.webIsOnline && result.isActiveVoice && !result.adminMode && !player.adminMode) {
                                if (
                                    result.player?.location != null && player.player?.location != null &&
                                    result.player?.location!!.world == player.player?.location!!.world
                                ) {
                                    val distance = player.player?.location?.distance(result.player?.location!!)
                                    if (distance != null) {
                                        val hearDistance = Storage.hearDistance
                                        var volume: Double
                                        if (distance < hearDistance) {
                                            if (!player.isSendOffer.contains(result.uuid)) {
                                                player.isSendOffer.add(result.uuid)
                                                if (!result.isSendOffer.contains(player.uuid)) {
                                                    socket.emit(
                                                        "onPlayerInDistancePlugin", mapOf(
                                                            "name" to result.name,
                                                            "uuid" to result.uuid,
                                                            "server" to result.server,
                                                            "socketID" to player.socketID
                                                        )
                                                    )
                                                }
                                            }

                                            volume = if (Storage.hearLazy) {
                                                (hearDistance - distance) / hearDistance
                                            } else {
                                                1.0
                                            }
                                            val resultSocketID = result.socketID
                                            if (resultSocketID != null) {
                                                if (player.adminMode) {
                                                    volume = 1.0
                                                }

                                                if (player.isMute) {
                                                    volume = 0.0
                                                }


                                                socket.emit(
                                                    "onPlayerVolumePlugin", mapOf<String, Any>(
                                                        "uuid" to player.uuid,
                                                        "volume" to volume,
                                                        "socketID" to resultSocketID
                                                    )
                                                )
                                            }
                                        } else {
                                            val socketID = result.socketID
                                            if (socketID != null && player.isSendOffer.contains(result.uuid)) {
                                                socket.emit(
                                                    "onPlayerOutDistancePlugin", mapOf(
                                                        "name" to player.name,
                                                        "uuid" to player.uuid,
                                                        "server" to player.server,
                                                        "socketID" to socketID
                                                    )
                                                )
                                                player.isSendOffer.remove(result.uuid)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }
            }.runTaskTimerAsynchronously(MelodyMine.instance, 0L, Storage.updateDistanceTime)
        }
    }
}