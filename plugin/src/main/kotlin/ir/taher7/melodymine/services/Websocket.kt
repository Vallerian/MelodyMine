package ir.taher7.melodymine.services


import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketIOException
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.listeners.SocketListener
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Settings
import ir.taher7.melodymine.utils.Utils
import java.net.URI


object Websocket {
    lateinit var socket: Socket
    fun connect() {
        try {
            val auth = mapOf(
                "from" to "plugin",
                "server" to Settings.server,
                "key" to Settings.pluginKey
            )

            val uri = URI.create(Utils.serverURL())
            val options = IO.Options.builder()
                .setAuth(auth)
                .setTransports(arrayOf("websocket"))
                .build()

            socket = IO.socket(uri, options)
            socket.connect()
            SocketListener(socket)


            socket.on(Socket.EVENT_CONNECT) {
                MelodyMine.instance.logger.info(Messages.getMessageString("success.websocket"))
                Database.updateSocketPlayer()
            }
            
            socket.on(Socket.EVENT_DISCONNECT) {
                MelodyMine.instance.logger.severe(Messages.getMessageString("errors.websocket"))
                Database.updateSocketPlayer()
                if (!socket.isActive) connect()
            }


        } catch (ex: SocketIOException) {
            MelodyMine.instance.logger.info(Messages.getMessageString("errors.websocket_failed"))
            if (!socket.isActive) connect()
            ex.printStackTrace()
        }
    }
}
