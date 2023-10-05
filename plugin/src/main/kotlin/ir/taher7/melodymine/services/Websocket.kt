package ir.taher7.melodymine.services


import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketIOException
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.listeners.SocketListener
import ir.taher7.melodymine.storage.Storage
import org.bukkit.scheduler.BukkitRunnable
import java.net.URI


object Websocket {
    lateinit var socket: Socket


    fun connect() {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val auth = mapOf(
                        "from" to "plugin",
                        "server" to Storage.server,
                        "key" to Storage.websocketKey
                    )

                    val uri = URI.create(Storage.websocket)
                    val options = IO.Options.builder()
                        .setAuth(auth)
                        .build()

                    socket = IO.socket(uri, options)
                    socket.connect()
                    SocketListener(socket)

                    socket.on(Socket.EVENT_CONNECT) {
                        MelodyMine.instance.logger.info("Websocket connected")
                        Database.updateSocketPlayer()
                    }

                    socket.on(Socket.EVENT_DISCONNECT) {
                        MelodyMine.instance.logger.severe("Websocket connection failed check your connection")
                        if (!socket.isActive) connect()
                    }

                } catch (ex: SocketIOException) {
                    MelodyMine.instance.logger.info("Websocket failed.")
                    if (!socket.isActive) connect()
                    ex.printStackTrace()
                }
            }
        }.runTaskAsynchronously(MelodyMine.instance)
    }
}