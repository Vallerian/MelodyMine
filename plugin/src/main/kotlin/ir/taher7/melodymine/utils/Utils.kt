package ir.taher7.melodymine.utils

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.core.MelodyManager
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.sendMessage
import ir.taher7.melodymine.utils.Adventure.showTitle
import ir.taher7.melodymine.utils.Adventure.toComponent
import net.kyori.adventure.title.Title
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapView
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.io.IOException
import java.time.Duration
import kotlin.random.Random


object Utils {

    private val qrCoreLore = listOf(
        "${ChatColor.GRAY}${ChatColor.STRIKETHROUGH}                                 ",
        "${ChatColor.WHITE}Step 1 ${ChatColor.DARK_GRAY}- ${ChatColor.DARK_AQUA}Scan the QRCode",
        "${ChatColor.WHITE}Step 2 ${ChatColor.DARK_GRAY}- ${ChatColor.DARK_AQUA}Click on the StartMelody Button.",
        "",
        "${ChatColor.DARK_RED}!!! ${ChatColor.RED}${ChatColor.BOLD}Don't Give this Item to another Player ${ChatColor.DARK_RED}!!!",
        "",
        "${ChatColor.YELLOW}Click to remove QRCode",
        "${ChatColor.GRAY}${ChatColor.STRIKETHROUGH}                                 ",
    )

    private val qrCodeDisplayName = "${ChatColor.AQUA}${ChatColor.ITALIC}MelodyMine QRCode"

    fun sendHelpMessage(player: Player) {
        player.sendMessage("<st><gradient:#121F31:#F04FE7>                     </gradient></st> <bold><gradient:#F04FE7:#FFF4E4:#F04FE7>MelodyMine</gradient></bold> <st><gradient:#F04FE7:#121F31>                      </st>".toComponent())
        player.sendMessage("")
        Storage.subCommands.forEach { subCommand: SubCommand ->
            if (player.hasPermission(subCommand.permission)) {
                player.sendMessage("<click:run_command:'${subCommand.syntax}'><hover:show_text:'<text_hover>Click to run this command <i>${subCommand.syntax}</i>'><text_hover>${subCommand.syntax} <#FFF4E4><bold>|</bold> <text>${subCommand.description}</hover></click>".toComponent())
            }
        }
        player.sendMessage("")
        player.sendMessage("<st><gradient:#121F31:#F04FE7:#121F31><st>                                                             ".toComponent())
    }

    fun getVerifyCode(): String {
        val length = 20
        val stringBuilder = StringBuilder(length)
        repeat(length) {
            val digit = Random.nextInt(10)
            stringBuilder.append(digit)
        }
        return stringBuilder.toString()
    }

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
                    if (Storage.forceVoiceTitle) {
                        player.player?.showTitle(
                            Title.title(
                                Storage.forceVoiceTitleMessage.toComponent(),
                                Storage.forceVoiceSubtitleMessage.toComponent(),
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
        }.runTaskTimer(MelodyMine.instance, 0L, 300L)
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
        if (!Storage.forceVoice) return true
        if (player.player?.hasPermission("melodymine.force") == true) return true
        return false
    }

    fun sendMessageLog(message: String, player: MelodyPlayer) {
        Storage.onlinePlayers.values.forEach { melodyPlayer ->
            if (melodyPlayer.player?.hasPermission("melodymine.toggle") == true && melodyPlayer.isToggle) {
                melodyPlayer.player?.sendMessage(message.replace("{PLAYER}", player.name).toComponent())
            }
        }
    }

    fun createQRCodeMap(view: MapView): ItemStack {
        val map = ItemStack(Material.FILLED_MAP)
        val mapMeta = map.itemMeta as MapMeta
        try {
            mapMeta.mapView = view
        } catch (ex: IOException) {
            mapMeta.mapId = view.id
        }

        mapMeta.setDisplayName(qrCodeDisplayName)
        mapMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, false)
        mapMeta.lore = qrCoreLore

        mapMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        mapMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

        map.itemMeta = mapMeta
        return map
    }

    fun isMap(item: ItemStack): Boolean {
        if (item.type != Material.FILLED_MAP) return false
        val itemMeta = item.itemMeta ?: return false
        if (itemMeta.displayName != qrCodeDisplayName) return false
        if (itemMeta.lore != qrCoreLore) return false
        return true
    }

    fun removeMap(player: Player) {
        if (isMap(player.inventory.itemInOffHand)) {
            player.inventory.setItemInOffHand(ItemStack(Material.AIR))
        }
        if (isMap(player.inventory.itemInMainHand)) {
            player.inventory.setItemInMainHand(ItemStack(Material.AIR))
        }
        if (player.inventory.firstEmpty() != -1) {
            player.inventory.forEach { itemStack ->
                if (itemStack != null && isMap(itemStack)) {
                    player.inventory.remove(itemStack)
                }
            }
        }

    }

    fun sendMelodyFiglet() {
        val consoleSender = MelodyMine.instance.server.consoleSender
        consoleSender.sendMessage("".toComponent())
        consoleSender.sendMessage("<gradient:#F04FE7:#FFF4E4>    __  ___     __          __      __  ____          ".toComponent())
        consoleSender.sendMessage("<gradient:#F04FE7:#FFF4E4>   /  |/  /__  / /___  ____/ /_  __/  |/  (_)___  ___ ".toComponent())
        consoleSender.sendMessage("<gradient:#F04FE7:#FFF4E4>  / /|_/ / _ \\/ / __ \\/ __  / / / / /|_/ / / __ \\/ _ \\".toComponent())
        consoleSender.sendMessage("<gradient:#F04FE7:#FFF4E4> / /  / /  __/ / /_/ / /_/ / /_/ / /  / / / / / /  __/".toComponent())
        consoleSender.sendMessage("<gradient:#F04FE7:#FFF4E4>/_/  /_/\\___/_/\\____/\\__,_/\\__, /_/  /_/_/_/ /_/\\___/ ".toComponent())
        consoleSender.sendMessage("<gradient:#F04FE7:#FFF4E4>                          /____/                      ".toComponent())
        consoleSender.sendMessage("".toComponent())
    }


}