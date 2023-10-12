package ir.taher7.melodymine.utils

import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.io.File
import javax.imageio.ImageIO

class QRCodeRenderer(private val file: File?) : MapRenderer() {
    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        val bufferedQR = ImageIO.read(file)
        bufferedQR?.let { canvas.drawImage(0, 0, it) }
    }
}
