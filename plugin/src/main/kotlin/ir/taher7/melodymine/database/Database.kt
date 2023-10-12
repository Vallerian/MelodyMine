package ir.taher7.melodymine.database

import com.cryptomorin.xseries.ReflectionUtils
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.sql.Connection
import java.util.function.Consumer


object Database {


    lateinit var hikari: HikariDataSource

    init {
        connect()
        initialize()
    }

    private fun connect() {

        try {
            val config = HikariConfig()
            config.setJdbcUrl("jdbc:mysql://${Storage.host}:${Storage.port}/${Storage.dbName}")
            if (ReflectionUtils.supports(13)) {
                config.driverClassName = "com.mysql.cj.jdbc.Driver"
            } else {
                config.driverClassName = "com.mysql.jdbc.Driver"
            }
            config.username = Storage.user
            config.password = Storage.password
            config.addDataSourceProperty("cachePrepStmts", "true")
            config.addDataSourceProperty("prepStmtCacheSize", "250")
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")


            hikari = HikariDataSource(config)
            MelodyMine.instance.logger.info("Successfully connected to database.")
        } catch (ex: Exception) {
            MelodyMine.instance.logger.severe("Database connection failed")
            ex.printStackTrace()
        }
    }

    private fun createConnection(): Connection? {
        return try {
            hikari.getConnection()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun closeConnection(connection: Connection) {
        try {
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initialize() {
        try {
            val connection = createConnection() ?: return
            val statement = connection.createStatement()
            statement.execute("CREATE TABLE IF NOT EXISTS melodymine(id INTEGER AUTO_INCREMENT PRIMARY KEY ,uuid VARCHAR(36) UNIQUE ,name VARCHAR(36),socketID VARCHAR(36) UNIQUE ,verifyCode VARCHAR(36) UNIQUE ,server VARCHAR(36),serverIp VARCHAR(36),webIp VARCHAR(36),isActiveVoice BOOLEAN default FALSE,isMute BOOLEAN default FALSE,serverIsOnline BOOLEAN default FALSE, webIsOnline BOOLEAN default FALSE)")
            closeConnection(connection)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun initPlayer(player: Player, consumer: Consumer<MelodyPlayer>) {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val connection = createConnection() ?: return
                    val statement = connection.prepareStatement(
                        "INSERT INTO melodymine(uuid,name,verifyCode,server,serverIsOnline) VALUES (?,?,?,?,?)"
                    )

                    val melodyPlayer = MelodyPlayer(
                        player = player.player,
                        uuid = player.player?.uniqueId.toString(),
                        name = player.name,
                        verifyCode = Utils.getVerifyCode(),
                        server = Storage.server,
                        serverIsOnline = true,
                    )

                    statement.setString(1, melodyPlayer.uuid)
                    statement.setString(2, melodyPlayer.name)
                    statement.setString(3, melodyPlayer.verifyCode)
                    statement.setString(4, melodyPlayer.server)
                    statement.setBoolean(5, melodyPlayer.serverIsOnline)
                    statement.executeUpdate()
                    consumer.accept(melodyPlayer)
                    closeConnection(connection)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }.runTaskAsynchronously(MelodyMine.instance)
    }

    fun findPlayer(uuid: String, consumer: Consumer<MelodyPlayer?>) {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val connection = createConnection() ?: return
                    val statement = connection.prepareStatement("SELECT * FROM melodymine WHERE uuid = ?")
                    statement.setString(1, uuid)
                    val result = statement.executeQuery()
                    if (result.next()) {
                        consumer.accept(
                            MelodyPlayer(
                                uuid = result.getString("uuid"),
                                name = result.getString("name"),
                                server = result.getString("server"),
                                socketID = result.getString("socketID"),
                                verifyCode = result.getString("verifyCode"),
                                serverIsOnline = result.getBoolean("serverIsOnline"),
                                webIsOnline = result.getBoolean("webIsOnline"),
                                isActiveVoice = result.getBoolean("isActiveVoice"),
                                isMute = result.getBoolean("isMute"),
                            )
                        )
                    } else {
                        consumer.accept(null)
                    }
                    closeConnection(connection)
                } catch (ex: Exception) {
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
                    val connection = createConnection() ?: return
                    if (!leave) {
                        val statement = connection.prepareStatement(
                            "UPDATE melodymine SET verifyCode = ?,server = ?, serverIsOnline = ?,isMute = ? WHERE uuid = ? LIMIT 1"
                        )
                        statement.setString(1, player.verifyCode)
                        statement.setString(2, player.server)
                        statement.setBoolean(3, player.serverIsOnline)
                        statement.setBoolean(4, player.isMute)
                        statement.setString(5, player.uuid)
                        statement.executeUpdate()
                    } else {
                        val statement = connection.prepareStatement(
                            "UPDATE melodymine SET verifyCode = ?,server = ?, serverIsOnline = ?,isMute = ? WHERE uuid = ? AND server = ?"
                        )
                        statement.setString(1, player.verifyCode)
                        statement.setString(2, player.server)
                        statement.setBoolean(3, player.serverIsOnline)
                        statement.setBoolean(4, player.isMute)
                        statement.setString(5, player.uuid)
                        statement.setString(6, Storage.server)
                        statement.executeUpdate()
                    }
                    closeConnection(connection)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }.runTaskAsynchronously(MelodyMine.instance)
    }

    fun resetDate() {
        val connection = createConnection() ?: return
        val statement = connection.prepareStatement(
            "UPDATE melodymine SET serverIsOnline = ? WHERE serverIsOnline = ? AND server = ?"
        )
        statement.setBoolean(1, false)
        statement.setBoolean(2, true)
        statement.setString(3, Storage.server)
        statement.executeUpdate()
        closeConnection(connection)
    }

    fun updateSocketPlayer() {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val connection = createConnection() ?: return
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
                    closeConnection(connection)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }.runTaskAsynchronously(MelodyMine.instance)
    }


    fun getVerifyCode(player: Player, consumer: Consumer<String>) {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val connection = createConnection() ?: return
                    val statement = connection.prepareStatement(
                        "SELECT verifyCode FROM melodymine WHERE uuid = ?"
                    )
                    statement.setString(1, player.uniqueId.toString())
                    val result = statement.executeQuery()
                    if (result.next()) {
                        consumer.accept(result.getString("verifyCode"))
                    } else {
                        val verifyCode = Utils.getVerifyCode()
                        val statement = connection.prepareStatement("UPDATE melodymine SET verifyCode = ? WHERE uuid = ? "
                        )
                        statement.setString(1, player.uniqueId.toString())
                        statement.executeUpdate()
                        consumer.accept(verifyCode)
                    }
                    closeConnection(connection)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }.runTaskAsynchronously(MelodyMine.instance)
    }

}