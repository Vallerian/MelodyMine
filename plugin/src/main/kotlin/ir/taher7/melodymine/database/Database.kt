package ir.taher7.melodymine.database

import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Timestamp
import java.util.function.Consumer

object Database {
    lateinit var connection: Connection
    private lateinit var host: String
    private lateinit var port: String
    private lateinit var user: String
    private lateinit var password: String
    private lateinit var dbName: String

    init {
        connect()
        initialize()
    }

    private fun connect() {
        val database = MelodyMine.instance.config.getConfigurationSection("database")
        if (database == null) {
            MelodyMine.instance.logger.severe("Database Config not found, Plugin disabled.")
            return
        }

        host = database.getString("host").toString()
        port = database.getString("port").toString()
        user = database.getString("user").toString()
        password = database.getString("password").toString()
        dbName = database.getString("database_name").toString()

        val url = "jdbc:mysql://${host}:${port}/${dbName}"
        try {
            connection = DriverManager.getConnection(url, user, password)
            MelodyMine.instance.logger.info("Successfully connected to database.")
        } catch (ex: SQLException) {
            MelodyMine.instance.logger.severe("Database connection failed,Plugin disabled.")
            MelodyMine.instance.server.pluginManager.disablePlugin(MelodyMine.instance)
            ex.printStackTrace()
        }
    }

    private fun initialize() {
        try {
            val statement = connection.createStatement()
            statement.execute("CREATE TABLE IF NOT EXISTS melodymine(id INTEGER AUTO_INCREMENT PRIMARY KEY ,uuid VARCHAR(36) UNIQUE ,name VARCHAR(36),socketID VARCHAR(36) UNIQUE ,verifyCode VARCHAR(36) UNIQUE ,server VARCHAR(36),serverIp VARCHAR(36),webIp VARCHAR(36),isActiveVoice BOOLEAN default FALSE,isMute BOOLEAN default FALSE,serverIsOnline BOOLEAN default FALSE, webIsOnline BOOLEAN default FALSE, serverLastLogin DATETIME, serverLastLogout DATETIME, webLastLogin DATETIME, webLastLogout DATETIME)")
            statement.close()
        } catch (ex: SQLException) {
            ex.printStackTrace()
        }
    }

    fun initPlayer(player: Player, consumer: Consumer<MelodyPlayer>) {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val statement = connection.prepareStatement(
                        "INSERT INTO melodymine(uuid,name,verifyCode,server,serverIp,serverIsOnline,serverLastLogin) VALUES (?,?,?,?,?,?,?)"
                    )

                    val melodyPlayer = MelodyPlayer(
                        player = player.player,
                        uuid = player.player?.uniqueId.toString(),
                        name = player.name,
                        verifyCode = Utils.getVerifyCode(),
                        serverIp = player.address?.address?.hostAddress,
                        server = Storage.server,
                        serverIsOnline = true,
                        serverLastLogin = Timestamp(System.currentTimeMillis()),
                    )

                    statement.setString(1, melodyPlayer.uuid)
                    statement.setString(2, melodyPlayer.name)
                    statement.setString(3, melodyPlayer.verifyCode)
                    statement.setString(4, melodyPlayer.server)
                    statement.setString(5, melodyPlayer.serverIp)
                    statement.setBoolean(6, melodyPlayer.serverIsOnline)
                    statement.setTimestamp(7, melodyPlayer.serverLastLogin)
                    statement.executeUpdate()
                    statement.close()
                    consumer.accept(melodyPlayer)
                } catch (ex: SQLException) {
                    ex.printStackTrace()
                }
            }
        }.runTaskAsynchronously(MelodyMine.instance)
    }

    fun findPlayer(uuid: String, consumer: Consumer<MelodyPlayer?>) {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val statement =
                        connection.prepareStatement("SELECT * FROM melodymine WHERE uuid = ? LIMIT 1")
                    statement.setString(1, uuid)
                    val result = statement.executeQuery()
                    if (result.next()) {
                        consumer.accept(
                            MelodyPlayer(
                                uuid = result.getString("uuid"),
                                name = result.getString("name"),
                                server = result.getString("server"),
                                socketID = result.getString("socketID"),
                                serverIp = result.getString("serverIp"),
                                webIp = result.getString("webIp"),
                                verifyCode = result.getString("verifyCode"),
                                serverIsOnline = result.getBoolean("serverIsOnline"),
                                webIsOnline = result.getBoolean("webIsOnline"),
                                isActiveVoice = result.getBoolean("isActiveVoice"),
                                isMute = result.getBoolean("isMute"),
                                serverLastLogin = result.getTimestamp("serverLastLogin"),
                                serverLastLogout = result.getTimestamp("serverLastLogout"),
                                webLastLogin = result.getTimestamp("webLastLogin"),
                                webLastLogout = result.getTimestamp("webLastLogout"),
                            )
                        )
                    } else {
                        consumer.accept(null)
                    }
                    statement.close()
                } catch (ex: SQLException) {
                    consumer.accept(null)
                    ex.printStackTrace()
                }
            }
        }.runTaskAsynchronously(MelodyMine.instance)
    }

    fun updatePlayer(player: MelodyPlayer, leave: Boolean) {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    if (!leave) {
                        val statement = connection.prepareStatement(
                            "UPDATE melodymine SET verifyCode = ?,server = ?, serverIp = ?,serverIsOnline = ?,isMute = ?,serverLastLogin = ?, serverLastLogout = ? WHERE uuid = ? LIMIT 1"
                        )
                        statement.setString(1, player.verifyCode)
                        statement.setString(2, player.server)
                        statement.setString(3, player.serverIp)
                        statement.setBoolean(4, player.serverIsOnline)
                        statement.setBoolean(5, player.isMute)
                        statement.setTimestamp(6, player.serverLastLogin)
                        statement.setTimestamp(7, player.serverLastLogin)
                        statement.setString(8, player.uuid)
                        statement.executeUpdate()
                    } else {
                        val statement = connection.prepareStatement(
                            "UPDATE melodymine SET verifyCode = ?,server = ?, serverIp = ?,serverIsOnline = ?,isMute = ?,serverLastLogin = ?, serverLastLogout = ? WHERE uuid = ? AND server = ? LIMIT 1"
                        )
                        statement.setString(1, player.verifyCode)
                        statement.setString(2, player.server)
                        statement.setString(3, player.serverIp)
                        statement.setBoolean(4, player.serverIsOnline)
                        statement.setBoolean(5, player.isMute)
                        statement.setTimestamp(6, player.serverLastLogin)
                        statement.setTimestamp(7, player.serverLastLogin)
                        statement.setString(8, player.uuid)
                        statement.setString(9, Storage.server)
                        statement.executeUpdate()
                    }

                } catch (ex: SQLException) {
                    ex.printStackTrace()
                }
            }
        }.runTaskAsynchronously(MelodyMine.instance)
    }

    fun resetDate() {
        val statement = connection.prepareStatement(
            "UPDATE melodymine SET serverIsOnline = ? WHERE serverIsOnline = ? AND server = ?"
        )
        statement.setBoolean(1, false)
        statement.setBoolean(2, true)
        statement.setString(3, Storage.server)
        statement.executeUpdate()
    }

    fun updateSocketPlayer() {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val uuidLIst = arrayListOf<String>()
                    Bukkit.getOnlinePlayers().forEach { player: Player ->
                        uuidLIst.add(player.uniqueId.toString())
                    }
                    if (uuidLIst.isNotEmpty()) {
                        val stringList = uuidLIst.joinToString(
                            separator = "','",
                            prefix = "'",
                            postfix = "'"
                        )
                        val statement = connection.prepareStatement(
                            "UPDATE melodymine SET serverIsOnline = ? WHERE melodymine.uuid IN (?)"
                        )
                        statement.setBoolean(1, true)
                        statement.setString(2, stringList)
                        statement.executeUpdate()
                    }
                } catch (ex: SQLException) {
                    ex.printStackTrace()
                }
            }
        }.runTaskAsynchronously(MelodyMine.instance)
    }


    fun getVerifyCode(player: Player, consumer: Consumer<String>) {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val statement = connection.prepareStatement(
                        "SELECT verifyCode FROM melodymine WHERE uuid = ? LIMIT 1"
                    )
                    statement.setString(1, player.uniqueId.toString())
                    val result = statement.executeQuery()
                    if (result.next()) {
                        consumer.accept(result.getString("verifyCode"))
                    } else {
                        val verifyCode = Utils.getVerifyCode()
                        val statement = connection.prepareStatement(
                            "UPDATE melodymine SET verifyCode = ? WHERE uuid = ? "
                        )
                        statement.setString(1, player.uniqueId.toString())
                        statement.executeUpdate()
                        consumer.accept(verifyCode)
                    }
                } catch (ex: SQLException) {
                    ex.printStackTrace()
                }
            }
        }.runTaskAsynchronously(MelodyMine.instance)
    }

}