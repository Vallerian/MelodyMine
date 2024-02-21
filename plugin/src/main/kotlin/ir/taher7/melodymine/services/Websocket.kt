package ir.taher7.melodymine.services


import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketIOException
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.database.Database
import ir.taher7.melodymine.listeners.SocketListener
import ir.taher7.melodymine.storage.Storage
import java.net.URI


object Websocket {
    lateinit var socket: Socket
    fun connect() {
        try {
            val auth = mapOf(
                "from" to "plugin",
                "server" to Storage.server,
                "key" to Storage.websocketKey
            )

            val uri = URI.create(Storage.websocket)
            val options = IO.Options.builder()
                .setAuth(auth)
                .setTransports(arrayOf("websocket"))
                .build()

            socket = IO.socket(uri, options)
            socket.connect()
            SocketListener(socket)


            socket.on(Socket.EVENT_CONNECT) {
                MelodyMine.instance.logger.info("Successfully connected to Websocket.")
                Database.updateSocketPlayer()
            }
            
            socket.on(Socket.EVENT_DISCONNECT) { args ->
                MelodyMine.instance.logger.severe("Websocket connection failed check your connection")
//                MelodyMine.instance.logger.severe("EVENT_DISCONNECT: ${args.joinToString(", ")}")
                Database.updateSocketPlayer()
                if (!socket.isActive) connect()
            }

//            socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
//                MelodyMine.instance.logger.severe("EVENT_CONNECT_ERROR: ${args.joinToString(", ")}")
//            }

        } catch (ex: SocketIOException) {
            MelodyMine.instance.logger.info("Websocket failed.")
            if (!socket.isActive) connect()
            ex.printStackTrace()
        }
    }
}
