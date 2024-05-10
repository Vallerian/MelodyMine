package ir.taher7.melodymine.database

import com.cryptomorin.xseries.ReflectionUtils
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.models.MelodyPlayer
import ir.taher7.melodymine.storage.Messages
import ir.taher7.melodymine.storage.Settings
import ir.taher7.melodymine.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.sql.Connection
import java.sql.Statement
import java.util.function.Consumer


object Database {


    lateinit var hikari: HikariDataSource

    init {
        try {
             connect()
             initialize()
         } catch (ex: Exception) {
             ex.printStackTrace()
         }
    }

    private fun connect() {

        try {
            val config = HikariConfig()
            config.setJdbcUrl("jdbc:mysql://${Settings.host}:${Settings.port}/${Settings.database}")
            if (ReflectionUtils.supports(13)) {
                config.driverClassName = "com.mysql.cj.jdbc.Driver"
            } else {
                config.driverClassName = "com.mysql.jdbc.Driver"
            }
            config.username = Settings.username
            config.password = Settings.password
            config.maximumPoolSize = 10

            hikari = HikariDataSource(config)
            MelodyMine.instance.logger.info(Messages.getMessageString("success.database"))
        } catch (ex: Exception) {
            MelodyMine.instance.logger.severe(Messages.getMessageString("errors.database"))
            ex.printStackTrace()
        }
    }

    private fun initialize() {
        if (!::hikari.isInitialized) return
        try {
            val connection = createConnection() ?: return
            val statement = connection.createStatement()
            statement.execute("CREATE TABLE IF NOT EXISTS melodymine(id INTEGER AUTO_INCREMENT PRIMARY KEY ,uuid VARCHAR(36) UNIQUE ,name VARCHAR(36),socketID VARCHAR(36) UNIQUE ,verifyCode VARCHAR(36) UNIQUE ,server VARCHAR(36),serverIp VARCHAR(36),webIp VARCHAR(36),isActiveVoice BOOLEAN default FALSE,isMute BOOLEAN default FALSE,serverIsOnline BOOLEAN default FALSE, webIsOnline BOOLEAN default FALSE)")
            closeConnection(connection)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun createConnection(): Connection? {
        if (!::hikari.isInitialized) return null
        return try {
            hikari.getConnection()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun closeConnection(connection: Connection) {
        if (!::hikari.isInitialized) return
        try {
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initPlayer(player: Player, consumer: Consumer<MelodyPlayer>) {
        if (!::hikari.isInitialized) return
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val connection = createConnection() ?: return
                    val statement = connection.prepareStatement(
                        "INSERT INTO melodymine(uuid,name,verifyCode,server,serverIsOnline) VALUES (?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS
                    )


                    val verifyCode = Utils.getVerifyCode()

                    statement.setString(1, player.player?.uniqueId.toString())
                    statement.setString(2, player.name)
                    statement.setString(3, verifyCode)
                    statement.setString(4, Settings.server)
                    statement.setBoolean(5, true)
                    statement.executeUpdate()

                    val generatedKeys = statement.generatedKeys
                    if (generatedKeys.next()) {
                        val id = generatedKeys.getLong(1)
                        val melodyPlayer = MelodyPlayer(
                            id = id.toInt(),
                            player = player.player,
                            uuid = player.player?.uniqueId.toString(),
                            name = player.name,
                            verifyCode = verifyCode,
                            server = Settings.server,
                            serverIsOnline = true,
                        )
                        consumer.accept(melodyPlayer)
                    }

                    closeConnection(connection)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }.runTaskAsynchronously(MelodyMine.instance)
    }

    fun findPlayer(uuid: String, consumer: Consumer<MelodyPlayer?>) {
        if (!::hikari.isInitialized) return
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
                                id = result.getInt("id"),
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
        if (!::hikari.isInitialized) return
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
                        statement.setString(6, Settings.server)
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
        if (!::hikari.isInitialized) return
        val connection = createConnection() ?: return
        val statement = connection.prepareStatement(
            "UPDATE melodymine SET serverIsOnline = ? WHERE serverIsOnline = ? AND server = ?"
        )
        statement.setBoolean(1, false)
        statement.setBoolean(2, true)
        statement.setString(3, Settings.server)
        statement.executeUpdate()
        closeConnection(connection)
    }


    fun updateSocketPlayer() {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val connection = createConnection() ?: return
                    val uuidList = arrayListOf<String>()
                    Bukkit.getOnlinePlayers().forEach { player: Player ->
                        uuidList.add(player.uniqueId.toString())
                    }
                    if (uuidList.isNotEmpty()) {
                        val placeholders = uuidList.joinToString(separator = ",", prefix = "(", postfix = ")") { "?" }
                        val sql = "UPDATE melodymine SET serverIsOnline = ? WHERE uuid IN $placeholders"
                        val statement = connection.prepareStatement(sql)
                        statement.setBoolean(1, true)
                        uuidList.forEachIndexed { index, uuid ->
                            statement.setString(index + 2, uuid)
                        }
                        statement.executeUpdate()
                    }
                    closeConnection(connection)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }.runTaskAsynchronously(MelodyMine.instance)
    }


    fun resetPlayerData(name: String, consumer: Consumer<Boolean>) {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    createConnection()?.use { connection ->
                        val query = "SELECT webIsOnline FROM melodymine WHERE LOWER(name) = ?"
                        connection.prepareStatement(query).use { statement ->
                            statement.setString(1, name)
                            val result = statement.executeQuery()
                            if (result.next()) {
                                val updateQuery = "UPDATE melodymine SET webIsOnline = false WHERE LOWER(name) = ?"
                                connection.prepareStatement(updateQuery).use { updateStatement ->
                                    updateStatement.setString(1, name)
                                    updateStatement.executeUpdate()
                                    consumer.accept(true)
                                }
                            } else {
                                consumer.accept(false)
                            }
                        }
                    }
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
                        val statement = connection.prepareStatement(
                            "UPDATE melodymine SET verifyCode = ? WHERE uuid = ? "
                        )
                        statement.setString(1, verifyCode)
                        statement.setString(2, player.uniqueId.toString())
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