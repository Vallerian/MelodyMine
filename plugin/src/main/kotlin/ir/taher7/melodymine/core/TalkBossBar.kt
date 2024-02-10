package ir.taher7.melodymine.core



import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Adventure.hideBossBar
import ir.taher7.melodymine.utils.Adventure.showBossBar
import ir.taher7.melodymine.utils.Adventure.toComponent
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.entity.Player


class TalkBossBar(val player: Player) {

    lateinit var bossBar: BossBar
    val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()]!!

    init {
        initTalkBossBar()
    }

    private fun initTalkBossBar() {
        val serverMute = Storage.bossbarConfigs["server-mute"] ?: return
        bossBar = BossBar.bossBar(
            if (melodyPlayer.isMute) serverMute.text.toComponent() else "".toComponent(),
            1f,
            if (melodyPlayer.isMute) BossBar.Color.RED else BossBar.Color.PURPLE,
            BossBar.Overlay.PROGRESS
        )

        if (serverMute.enable && melodyPlayer.isMute && !Storage.isEnableBossBar) {
            showTalkBossBar()
        } else {
            hideTalkBossBar()
        }

    }

    fun showTalkBossBar() {
        player.showBossBar(bossBar)
    }

    fun hideTalkBossBar() {
        player.hideBossBar(bossBar)
    }

    fun setBossBarActive() {
        val active = Storage.bossbarConfigs["active"] ?: return
        val color = BossBar.Color.NAMES.value(active.color.lowercase()) ?: BossBar.Color.WHITE
        if (active.enable) {
            showTalkBossBar()
            bossBar.name(active.text.toComponent())
            bossBar.color(color)
        } else {
            hideTalkBossBar()
        }
    }

    fun setBossBarInactive() {
        val inactive = Storage.bossbarConfigs["inactive"] ?: return
        val color = BossBar.Color.NAMES.value(inactive.color.lowercase()) ?: BossBar.Color.WHITE
        if (inactive.enable) {
            showTalkBossBar()
            bossBar.name(inactive.text.toComponent())
            bossBar.color(color)
        } else {
            hideTalkBossBar()
        }
    }

    fun setBossBarSelfMute() {
        val selfMute = Storage.bossbarConfigs["self-mute"] ?: return
        val color = BossBar.Color.NAMES.value(selfMute.color.lowercase()) ?: BossBar.Color.WHITE
        if (selfMute.enable) {
            showTalkBossBar()
            bossBar.name(selfMute.text.toComponent())
            bossBar.color(color)
        } else {
            hideTalkBossBar()
        }
    }

    fun setBossBarServerMute() {
        val serverMute = Storage.bossbarConfigs["server-mute"] ?: return
        val color = BossBar.Color.NAMES.value(serverMute.color.lowercase()) ?: BossBar.Color.WHITE
        if (serverMute.enable) {
            showTalkBossBar()
            bossBar.name(serverMute.text.toComponent())
            bossBar.color(color)
        } else {
            hideTalkBossBar()
        }
    }


}