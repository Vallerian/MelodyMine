package ir.taher7.melodymine.utils

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.commands.SubCommand
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.AdventureUtils.sendMessage
import ir.taher7.melodymine.utils.AdventureUtils.toComponent
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.random.Random


object Utils {
    fun sendHelpMessage(player: Player) {
        player.sendMessage("<st><gradient:#121F31:#F04FE7>                     </gradient></st> <bold><gradient:#F04FE7:#FFF4E4:#F04FE7>MelodyMine</gradient></bold> <st><gradient:#F04FE7:#121F31>                      </st>".toComponent())
        player.sendMessage("")
        Storage.subCommands.forEach { subCommand: SubCommand ->
            if (player.hasPermission(subCommand.permission)) {
                player.sendMessage("<click:run_command:'${subCommand.syntax}'><hover:show_text:'<hover_text>Click to run this command <i>${subCommand.syntax}</i>'><text_prefix>${subCommand.syntax}</gradient> <#FFF4E4><bold>|</bold> <text>${subCommand.description}</hover></click>".toComponent())
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

    fun timeAgo(timestamp: Timestamp): String {
        val currentTime = System.currentTimeMillis()
        val timestampTime = timestamp.time
        val timeDifference = abs(currentTime - timestampTime)

        val seconds = timeDifference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 7 -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp)
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "Just now"
        }
    }

    fun forceVoice(player: MelodyPlayer) {
        if (!Storage.forceVoice || player.isActiveVoice || player.player?.hasPermission("melodymine.force") == true) return
        object : BukkitRunnable() {
            override fun run() {
                if (player.isActiveVoice || !Storage.forceVoice || player.player?.hasPermission("melodymine.force") == true) {
                    cancel()
                    return
                }

                object : BukkitRunnable() {
                    override fun run() {
                        if (player.isActiveVoice || !Storage.forceVoice || player.player?.hasPermission("melodymine.force") == true) {
                            player.player?.removePotionEffect(PotionEffectType.BLINDNESS)
                            cancel()
                            return
                        }
                        player.player?.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 200, 1))
                    }
                }.runTaskTimer(MelodyMine.instance, 0L, 10L)

                player.player?.sendMessage("<click:run_command:'/melodymine start'><hover:show_text:'<hover_text>Click to run this command <i>/melodymine start</i>'><prefix>You must active your voice chat. do <i>/melodymine start</i></hover></click>".toComponent())
            }
        }.runTaskTimer(MelodyMine.instance, 0L, 300L)
    }

    fun sendMessageLog(message: String, player: MelodyPlayer) {
        Storage.onlinePlayers.values.forEach { melodyPlayer ->
            if (melodyPlayer.player?.hasPermission("melodymine.toggle") == true && melodyPlayer.isToggle) {
                melodyPlayer.player?.sendMessage(message.replace("{PLAYER}", player.name).toComponent())
            }
        }
    }

    fun sendMelodyFiglet() {
        val consoleSender = MelodyMine.instance.server.consoleSender
        consoleSender.sendMessage("".toComponent())
        consoleSender.sendMessage("<text>    __  ___     __          __      __  ____          ".toComponent())
        consoleSender.sendMessage("<text>   /  |/  /__  / /___  ____/ /_  __/  |/  (_)___  ___ ".toComponent())
        consoleSender.sendMessage("<text>  / /|_/ / _ \\/ / __ \\/ __  / / / / /|_/ / / __ \\/ _ \\".toComponent())
        consoleSender.sendMessage("<text> / /  / /  __/ / /_/ / /_/ / /_/ / /  / / / / / /  __/".toComponent())
        consoleSender.sendMessage("<text>/_/  /_/\\___/_/\\____/\\__,_/\\__, /_/  /_/_/_/ /_/\\___/ ".toComponent())
        consoleSender.sendMessage("<text>                          /____/                      ".toComponent())
        consoleSender.sendMessage("".toComponent())
    }
}