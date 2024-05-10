package ir.taher7.melodymine.listeners

import com.google.gson.GsonBuilder
import io.socket.client.Socket
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.api.events.*
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.models.MelodyControl
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.models.MelodyTalk
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Settings
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendActionbar
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable


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
            if (melodyPlayer.server != Settings.server) return@on
            updateMelodyPlayer(melodyPlayer)
            Storage.onlinePlayers[melodyPlayer.uuid]?.socketID?.let { MelodyManager.sendSoundSetting(it) }
            Storage.onlinePlayers[melodyPlayer.uuid]?.isSendOffer = arrayListOf()
            Storage.onlinePlayers.values.forEach { player ->
                if (player.isSendOffer.contains(melodyPlayer.uuid)) {
                    player.isSendOffer.remove(melodyPlayer.uuid)
                }
            }

            Utils.sendMessageLog(Messages.getMessageString("logger.website_join"), melodyPlayer)
            val player = Storage.onlinePlayers[melodyPlayer.uuid]?.player
            if (player != null) {
                when (Settings.statusJoinWeb) {
                    "message" -> {
                        player.sendMessage(Messages.getMessage("website.join"))
                    }

                    "actionbar" -> {
                        player.sendActionbar(Messages.getMessage("website.join"))
                    }

                    else -> {}
                }
                object : BukkitRunnable() {
                    override fun run() {
                        Utils.removeMap(player)
                    }
                }.runTask(MelodyMine.instance)
            }

            object : BukkitRunnable() {
                override fun run() {
                    val hasPlayer = Storage.onlinePlayers[melodyPlayer.uuid]
                    if (hasPlayer != null) {
                        Bukkit.getServer().pluginManager.callEvent(PlayerJoinWebEvent(hasPlayer))
                    }
                }
            }.runTask(MelodyMine.instance)
        }

        socket.on("onNewPlayerLeaveWeb") { args ->
            val melodyPlayer = gson.fromJson(args[0].toString(), MelodyPlayer::class.java)
            if (melodyPlayer.server != Settings.server) return@on
            updateMelodyPlayer(melodyPlayer)

            Utils.clearUpCall(Storage.onlinePlayers[melodyPlayer.uuid])
            Storage.onlinePlayers[melodyPlayer.uuid]?.talkBossBar?.hideTalkBossBar()
            Storage.onlinePlayers[melodyPlayer.uuid]?.talkNameTag?.clearNameTag()

            val targetForce = Storage.onlinePlayers[melodyPlayer.uuid]
            if (targetForce != null) Utils.forceVoice(targetForce)

            Storage.onlinePlayers[melodyPlayer.uuid]?.isSendOffer = arrayListOf()
            Storage.onlinePlayers.values.forEach { player ->
                if (player.isSendOffer.contains(melodyPlayer.uuid)) {
                    player.isSendOffer.remove(melodyPlayer.uuid)
                }
            }

            Utils.sendMessageLog(Messages.getMessageString("logger.website_leave"), melodyPlayer)
            val player = Storage.onlinePlayers[melodyPlayer.uuid]?.player
            if (player != null) {
                when (Settings.statusLeaveWeb) {
                    "message" -> {
                        player.sendMessage(Messages.getMessage("website.leave"))
                    }

                    "actionbar" -> {
                        player.sendActionbar(Messages.getMessage("website.leave"))
                    }

                    else -> {}
                }
            }
            object : BukkitRunnable() {
                override fun run() {
                    val hasPlayer = Storage.onlinePlayers[melodyPlayer.uuid]
                    if (hasPlayer != null) {
                        Bukkit.getServer().pluginManager.callEvent(PlayerLeaveWebEvent(hasPlayer))
                    }
                }
            }.runTask(MelodyMine.instance)
        }

        socket.on("onPlayerStartVoiceWeb") { args ->
            val melodyPlayer = gson.fromJson(args[0].toString(), MelodyPlayer::class.java)
            if (melodyPlayer.server != Settings.server) return@on

            updateMelodyPlayer(melodyPlayer)
            Storage.onlinePlayers[melodyPlayer.uuid]?.isSendOffer = arrayListOf()
            Storage.onlinePlayers[melodyPlayer.uuid]?.adminMode = false
            Storage.onlinePlayers[melodyPlayer.uuid]?.isSelfMute = true
            Storage.onlinePlayers[melodyPlayer.uuid]?.isDeafen = true
            Storage.onlinePlayers[melodyPlayer.uuid]?.talkBossBar?.showTalkBossBar()
            Storage.onlinePlayers[melodyPlayer.uuid]?.talkNameTag?.clearNameTag()
            Storage.onlinePlayers[melodyPlayer.uuid]?.socketID?.let { MelodyManager.sendSoundSetting(it) }

            Storage.onlinePlayers.values.forEach { player ->
                if (player.isSendOffer.contains(melodyPlayer.uuid)) {
                    player.isSendOffer.remove(melodyPlayer.uuid)
                }
            }

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
            Utils.sendMessageLog(Messages.getMessageString("logger.start_voice"), melodyPlayer)
            val player = Storage.onlinePlayers[melodyPlayer.uuid]?.player
            if (player != null) {
                when (Settings.statusStartVoice) {
                    "message" -> {
                        player.sendMessage(Messages.getMessage("website.start"))
                    }

                    "actionbar" -> {
                        player.sendActionbar(Messages.getMessage("website.start"))
                    }

                    else -> {}
                }
                if (Settings.forceVoice && !player.hasPermission("melodymine.force")) {
                    Storage.onlinePlayers[melodyPlayer.uuid]?.let { Utils.clearForceVoice(it) }
                }
            }
            object : BukkitRunnable() {
                override fun run() {
                    val hasPlayer = Storage.onlinePlayers[melodyPlayer.uuid]
                    if (hasPlayer != null) {
                        Bukkit.getServer().pluginManager.callEvent(PlayerStartVoiceEvent(hasPlayer))
                    }
                }
            }.runTask(MelodyMine.instance)
        }

        socket.on("onPlayerEndVoiceWeb") { args ->
            val melodyPlayer = gson.fromJson(args[0].toString(), MelodyPlayer::class.java)
            if (melodyPlayer.server != Settings.server) return@on

            Utils.clearUpCall(Storage.onlinePlayers[melodyPlayer.uuid])
            Storage.onlinePlayers[melodyPlayer.uuid]?.talkBossBar?.hideTalkBossBar()
            Storage.onlinePlayers[melodyPlayer.uuid]?.talkNameTag?.clearNameTag()

            Storage.onlinePlayers[melodyPlayer.uuid]?.isSendOffer = arrayListOf()
            Storage.onlinePlayers.values.forEach { player ->
                if (player.isSendOffer.contains(melodyPlayer.uuid)) {
                    player.isSendOffer.remove(melodyPlayer.uuid)
                }
            }
            updateMelodyPlayer(melodyPlayer)
            Storage.onlinePlayers[melodyPlayer.uuid]?.adminMode = false
            Utils.sendMessageLog(Messages.getMessageString("logger.end_voice"), melodyPlayer)
            val targetForce = Storage.onlinePlayers[melodyPlayer.uuid]
            if (targetForce != null) Utils.forceVoice(targetForce)
            val player = Storage.onlinePlayers[melodyPlayer.uuid]?.player
            if (player != null) {
                when (Settings.statusEndVoice) {
                    "message" -> {
                        player.sendMessage(Messages.getMessage("website.end"))
                    }

                    "actionbar" -> {
                        player.sendActionbar(Messages.getMessage("website.end"))
                    }

                    else -> {}
                }
            }
            object : BukkitRunnable() {
                override fun run() {
                    val hasPlayer = Storage.onlinePlayers[melodyPlayer.uuid]
                    if (hasPlayer != null) {
                        Bukkit.getServer().pluginManager.callEvent(PlayerEndVoiceEvent(hasPlayer))
                    }
                }
            }.runTask(MelodyMine.instance)
        }

        socket.on("onPlayerChangeServerToWeb") { args ->
            val melodyPlayer = gson.fromJson(args[0].toString(), MelodyPlayer::class.java)
            if (melodyPlayer.server == Settings.server) {
                updateMelodyPlayer(melodyPlayer)
                Storage.onlinePlayers[melodyPlayer.uuid]?.isSendOffer = arrayListOf()
                Storage.onlinePlayers.values.forEach { player ->
                    if (player.isSendOffer.contains(melodyPlayer.uuid)) {
                        player.isSendOffer.remove(melodyPlayer.uuid)
                    }
                }
                Utils.sendMessageLog(Messages.getMessageString("logger.active_voice"), melodyPlayer)

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

        socket.on("onPlayerChangeControlReceive") { args ->
            val melodyControl = gson.fromJson(args[0].toString(), MelodyControl::class.java)
            if (melodyControl.server != Settings.server) return@on
            val melodyPlayer = Storage.onlinePlayers[melodyControl.uuid] ?: return@on
            melodyPlayer.updateControl(melodyControl)

            object : BukkitRunnable() {
                override fun run() {
                    Bukkit.getServer().pluginManager.callEvent(PlayerChangeControlWebEvent(melodyPlayer, melodyControl))
                }
            }.runTask(MelodyMine.instance)
        }

        socket.on("onPlayerTalkReceive") { args ->
            val melodyTalk = gson.fromJson(args[0].toString(), MelodyTalk::class.java)
            if (melodyTalk.server != Settings.server) return@on
            val playerTalk = Storage.onlinePlayers[melodyTalk.uuid] ?: return@on
            Storage.onlinePlayers[melodyTalk.uuid]?.isTalk = melodyTalk.isTalk

            MelodyManager.showPlayerIsTalking(playerTalk)

            object : BukkitRunnable() {
                override fun run() {
                    Bukkit.getServer().pluginManager.callEvent(PlayerChangeTalkEvent(playerTalk, melodyTalk))
                }
            }.runTask(MelodyMine.instance)
        }
    }

    private fun updateMelodyPlayer(melodyPlayer: MelodyPlayer) {
        if (melodyPlayer.server == Settings.server) {
            Storage.onlinePlayers[melodyPlayer.uuid]?.updateWebData(melodyPlayer)
        }
    }

}