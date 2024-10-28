package ir.taher7.melodymine.utils

import com.cryptomorin.xseries.reflection.XReflection
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Settings
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendComponent
import ir.taher7.melodymine.utils.Adventure.showTitle
import ir.taher7.melodymine.utils.Adventure.toComponent
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.title.Title
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapView
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.io.IOException
import java.time.Duration
import kotlin.random.Random

object Utils {
    fun sendHelpMessage(player: Player) {
        player.sendComponent(Messages.getMessage("general.content_header"))
        player.sendComponent("")
        Storage.subCommands.forEach { subCommand: SubCommand ->
            if (player.hasPermission(subCommand.permission)) {
                player.sendComponent(
                    Messages.getMessage(
                        "general.help_line", hashMapOf(
                            "{SYNTAX}" to subCommand.syntax,
                            "{DESCRIPTION}" to subCommand.description,
                        )
                    )
                )
            }
        }
        player.sendComponent("")
        player.sendComponent(Messages.getMessage("general.content_footer"))
    }

    fun getVerifyCode(length: Int = 20): String = (1..length).joinToString("") { Random.nextInt(10).toString() }

    fun forceVoice(melodyPlayer: MelodyPlayer) {
        if (checkPlayerForce(melodyPlayer)) return
        object : BukkitRunnable() {
            override fun run() {
                val player = Storage.onlinePlayers[melodyPlayer.uuid]
                if (player == null) {
                    cancel()
                    return
                }
                if (checkPlayerForce(player)) {
                    clearForceVoice(player)
                    cancel()
                } else {
                    if (Settings.forceVoiceTitle) {
                        val forceVoiceTitleText = parsePlaceholder(player.player, Messages.getMessage("force_voice.title"))
                        val forceVoiceSubTitleText = parsePlaceholder(player.player, Messages.getMessage("force_voice.subtitle"))

                        player.player?.showTitle(
                            Title.title(
                                forceVoiceTitleText.toComponent(),
                                forceVoiceSubTitleText.toComponent(),
                                Title.Times.times(
                                    Duration.ofMillis(100),
                                    Duration.ofDays(365),
                                    Duration.ofMillis(100)
                                )
                            )
                        )
                    }
                    player.player?.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 500, 1))
                    MelodyManager.sendStartLink(player.player!!)
                }
            }
        }.runTaskTimer(MelodyMine.instance, 0L, Settings.forceVoiceInterval)
    }

    fun clearForceVoice(player: MelodyPlayer) {
        if (!checkPlayerForce(player)) return
        object : BukkitRunnable() {
            override fun run() {
                player.player?.removePotionEffect(PotionEffectType.BLINDNESS)
                player.player?.resetTitle()
            }
        }.runTask(MelodyMine.instance)
    }

    fun checkPlayerForce(player: MelodyPlayer): Boolean {
        if (player.isActiveVoice) return true
        if (!Settings.forceVoice) return true
        if (player.player?.hasPermission("melodymine.force") == true) return true
        if (Settings.disableWorlds.contains(player.player?.location?.world?.name)) return true
        return false
    }

    fun sendMessageLog(message: String, player: MelodyPlayer) {
        object : BukkitRunnable() {
            override fun run() {
                Storage.onlinePlayers.values.forEach { melodyPlayer ->
                    if (melodyPlayer.player?.hasPermission("melodymine.toggle") == true && melodyPlayer.isToggle) {
                        melodyPlayer.player?.sendComponent(message.replace("{PLAYER}", player.name))
                    }
                }
            }
        }.runTask(MelodyMine.instance)
    }

    fun createQRCodeMap(view: MapView): ItemStack {
        val map = ItemStack(Material.FILLED_MAP)
        val mapMeta = map.itemMeta as MapMeta
        try {
            mapMeta.mapView = view
        } catch (ex: IOException) {
            mapMeta.mapId = view.id
        }

        mapMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Messages.qrcodeDisplayName))
        mapMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, false)
        mapMeta.lore = Messages.qrcodeLore.map { lore -> ChatColor.translateAlternateColorCodes('&', lore) }

        mapMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        mapMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

        if (XReflection.supports(14)) {
            mapMeta.persistentDataContainer.set(
                NamespacedKey(MelodyMine.instance, "qrcode"),
                PersistentDataType.INTEGER,
                1
            )
        }

        map.itemMeta = mapMeta
        return map
    }

    fun isMap(item: ItemStack): Boolean {
        if (XReflection.supports(14)) {
            val mapMeta = item.itemMeta ?: return false
            mapMeta.persistentDataContainer.get(
                NamespacedKey(MelodyMine.instance, "qrcode"),
                PersistentDataType.INTEGER,
            ) ?: return false
            return true
        } else {
            if ((XReflection.supports(13) && item.type != Material.FILLED_MAP) || (!XReflection.supports(13) && item.type != Material.MAP)) return false
            val itemMeta = item.itemMeta ?: return false
            if (itemMeta.displayName != ChatColor.translateAlternateColorCodes('&', Messages.qrcodeDisplayName)) return false
            if (itemMeta.lore != Messages.qrcodeLore) return false
            return true
        }
    }

    fun removeMap(player: Player) {
        if (isMap(player.inventory.itemInOffHand)) player.inventory.setItemInOffHand(ItemStack(Material.AIR))
        if (isMap(player.inventory.itemInMainHand)) player.inventory.setItemInMainHand(ItemStack(Material.AIR))
        if (player.inventory.firstEmpty() != -1) player.inventory.forEach { itemStack ->
            if (itemStack != null && isMap(itemStack)) player.inventory.remove(itemStack)
        }
    }

    fun clearUpCall(melodyPlayer: MelodyPlayer?, isQuit: Boolean = false) {
        object : BukkitRunnable() {
            override fun run() {
                if (melodyPlayer == null) return

                if (melodyPlayer.isCallPending) {
                    val targetPlayer = melodyPlayer.callPendingTarget ?: return

                    melodyPlayer.isCallPending = false
                    melodyPlayer.callPendingTarget = null

                    targetPlayer.isCallPending = false
                    targetPlayer.callPendingTarget = null

                    melodyPlayer.pendingTask?.cancel()
                    targetPlayer.pendingTask?.cancel()

                    melodyPlayer.pendingTask = null
                    targetPlayer.pendingTask = null

                    if (isQuit) MelodyManager.endPendingCall(melodyPlayer, targetPlayer)
                    targetPlayer.player?.sendComponent(Messages.getMessage("commands.call.call_pending_end"))
                }

                if (melodyPlayer.isInCall) {
                    val targetPlayer = melodyPlayer.callTarget ?: return
                    melodyPlayer.isInCall = false
                    melodyPlayer.callTarget = null
                    targetPlayer.isInCall = false
                    targetPlayer.callTarget = null
                    if (isQuit) MelodyManager.endCall(melodyPlayer, targetPlayer)
                    targetPlayer.player?.sendComponent(
                        Messages.getMessage(
                            "commands.call.call_end",
                            hashMapOf("{PLAYER}" to melodyPlayer.name)
                        )
                    )
                }

            }
        }.runTask(MelodyMine.instance)
    }

    fun checkPlayerCoolDown(player: Player): Boolean {
        if (Storage.commandCoolDown.containsKey(player.uniqueId) && (System.currentTimeMillis() - Storage.commandCoolDown[player.uniqueId]!!) <= Settings.commandsCoolDown) {
            player.sendComponent(
                Messages.getMessage(
                    "general.cool_down",
                    hashMapOf("{TIME}" to ((Settings.commandsCoolDown - (System.currentTimeMillis() - Storage.commandCoolDown[player.uniqueId]!!)) / 1000))
                )
            )
            return true
        }
        return false
    }

    fun resetPlayerCoolDown(player: Player) {
        Storage.commandCoolDown[player.uniqueId] = System.currentTimeMillis()
    }

    fun removePlayerCoolDown(player: Player) = Storage.commandCoolDown.remove(player.uniqueId)

    fun clientURL() = "http${if (Settings.domain !in setOf("localhost", "0.0.0.0", "127.1.1.0")) "s" else ""}://${Settings.domain}:${Settings.clientPort}"

    fun serverURL() = "http${if (Settings.ssl && Settings.wsDomain !in setOf("localhost", "0.0.0.0", "127.1.1.0")) "s" else ""}://${Settings.wsDomain}:${Settings.serverPort}"

    fun parsePlaceholder(player: Player?, string: String): String {
        if (MelodyMine.instance.server.pluginManager.getPlugin("PlaceholderAPI") == null) return string
        return PlaceholderAPI.setPlaceholders(player, string)
    }

    fun sendMelodyFiglet() {
        val consoleSender = MelodyMine.instance.server.consoleSender
        consoleSender.sendComponent("")
        consoleSender.sendComponent("<gradient:#F04FE7:#FFF4E4>    __  ___     __          __      __  ____          ")
        consoleSender.sendComponent("<gradient:#F04FE7:#FFF4E4>   /  |/  /__  / /___  ____/ /_  __/  |/  (_)___  ___ ")
        consoleSender.sendComponent("<gradient:#F04FE7:#FFF4E4>  / /|_/ / _ \\/ / __ \\/ __  / / / / /|_/ / / __ \\/ _ \\")
        consoleSender.sendComponent("<gradient:#F04FE7:#FFF4E4> / /  / /  __/ / /_/ / /_/ / /_/ / /  / / / / / /  __/")
        consoleSender.sendComponent("<gradient:#F04FE7:#FFF4E4>/_/  /_/\\___/_/\\____/\\__,_/\\__, /_/  /_/_/_/ /_/\\___/ ")
        consoleSender.sendComponent("<gradient:#F04FE7:#FFF4E4>                          /____/                      ")
        consoleSender.sendComponent("")
    }
}
