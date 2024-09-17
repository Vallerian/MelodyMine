package ir.taher7.melodymine.core

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot
import com.comphenix.protocol.wrappers.Pair
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry
import com.cryptomorin.xseries.reflection.XReflection
import ir.taher7.melodymine.MelodyMine
import ir.taher7.melodymine.models.NameTagConfig
import ir.taher7.melodymine.storage.Storage
import ir.taher7.melodymine.storage.Talk
import ir.taher7.melodymine.utils.Adventure.toComponent
import ir.taher7.melodymine.utils.Utils
import ir.taher7.melodymine.utils.Utils.parsePlaceholder
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.lang.reflect.InvocationTargetException
import java.util.*
import kotlin.properties.Delegates


class TalkNameTag(val player: Player) {
    var nameTagId by Delegates.notNull<Int>()
    lateinit var nameTagUUID: UUID
    lateinit var nameTagType: String

    val melodyPlayer = Storage.onlinePlayers[player.uniqueId.toString()]!!


    init {
        initNameTags()
    }

    private fun initNameTags() {
        nameTagId = Utils.getVerifyCode(9).toInt()
        nameTagUUID = UUID.randomUUID()
        nameTagType = if (melodyPlayer.isMute) "server_mute" else "inactive"
        refreshNameTag()

        try {
            for (uuid in melodyPlayer.isSendOffer) {
                val target = Storage.onlinePlayers[uuid] ?: continue
                if (target.uuid != player.uniqueId.toString()) {
                    val targetNameTag = target.talkNameTag ?: continue

                    val packet = createNameTagPacket(
                        targetNameTag.nameTagId,
                        targetNameTag.nameTagUUID,
                        target.player!!,
                        targetNameTag.getConfig()!!
                    )

                    val metadataPacket = createNameTagMetaDataPacket(
                        targetNameTag.nameTagId,
                        targetNameTag.getConfig()!!
                    )

                    val itemPacket = createNameTagItemPacket(
                        targetNameTag.nameTagId,
                        targetNameTag.getConfig()!!
                    )

                    if (packet != null) {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
                    }
                    if (metadataPacket != null) {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(player, metadataPacket)
                    }
                    if (itemPacket != null) {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(player, itemPacket)
                    }
                }
            }
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

    }


    private fun createNameTagPacket(id: Int, uuid: UUID, player: Player, config: NameTagConfig): PacketContainer? {
        if (!config.enable) return null

        val packet = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY)
        packet.integers.writeSafely(0, id)
        packet.uuiDs.writeSafely(0, uuid)
        packet.entityTypeModifier.writeSafely(0, EntityType.ARMOR_STAND)
        packet.doubles
            .writeSafely(0, player.location.x + config.position.x)
            .writeSafely(1, player.location.y + config.position.y)
            .writeSafely(2, player.location.z + config.position.z)

        return packet
    }

    private fun createNameTagMetaDataPacket(id: Int, config: NameTagConfig): PacketContainer? {
        if (!config.enable) return null

        val packet = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)

        packet.integers.writeSafely(0, id)
        val text = parsePlaceholder(player, config.text)
        if (XReflection.supports(19, 3)) {

            packet.dataValueCollectionModifier.writeSafely(
                0, listOf(
                    WrappedDataValue(
                        2,
                        Registry.getChatComponentSerializer(true),
                        Optional.of(MinecraftComponentSerializer.get().serialize(text.toComponent()))
                    ),
                    WrappedDataValue(3, Registry.get(Boolean::class.javaObjectType), config.textVisible),
                    WrappedDataValue(0, Registry.get(Byte::class.javaObjectType), 0x20.toByte()),
                    WrappedDataValue(15, Registry.get(Byte::class.javaObjectType), (0x01 or 0x08 or 0x10).toByte()),
                )
            )

        } else {

            val metadata = WrappedDataWatcher()
            val byteSerializer = Registry.get(Byte::class.javaObjectType)
            val booleanSerializer = Registry.get(Boolean::class.javaObjectType)
            val chatSerializer = Registry.getChatComponentSerializer(true)

            val invisible = WrappedDataWatcher.WrappedDataWatcherObject(0, byteSerializer)
            val displayName = WrappedDataWatcher.WrappedDataWatcherObject(2, chatSerializer)
            val displayNameVisible = WrappedDataWatcher.WrappedDataWatcherObject(3, booleanSerializer)
            val base = WrappedDataWatcher.WrappedDataWatcherObject(15, byteSerializer)


            metadata.setObject(invisible, 0x20.toByte())
            metadata.setObject(
                displayName,
                Optional.of(MinecraftComponentSerializer.get().serialize(text.toComponent()))
            )
            metadata.setObject(displayNameVisible, config.textVisible)
            metadata.setObject(base, (0x01 or 0x08 or 0x10).toByte())
            packet.watchableCollectionModifier.write(0, metadata.watchableObjects)
        }

        return packet
    }

    private fun createNameTagItemPacket(id: Int, config: NameTagConfig): PacketContainer? {
        val packet = PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT)
        packet.integers.writeSafely(0, id)
        packet.itemSlots.writeSafely(0, ItemSlot.HEAD)
        val item = Material.getMaterial(config.item.type.uppercase())?.let { ItemStack(it) } ?: return null
        val itemMeta = item.itemMeta ?: return null
        itemMeta.setCustomModelData(config.item.customData)
        item.setItemMeta(itemMeta)
        packet.slotStackPairLists.write(0, listOf(Pair(ItemSlot.HEAD, item)))

        return packet
    }


    fun setNameTagActive() {
        if (nameTagType == "active") return
        nameTagType = "active"
        refreshNameTag()
    }

    fun setNameTagInactive() {
        if (nameTagType == "inactive") return
        nameTagType = "inactive"
        refreshNameTag()
    }

    fun setNameTagSelfMute() {
        if (nameTagType == "self_mute") return
        nameTagType = "self_mute"
        refreshNameTag()
    }

    fun setNameTagServerMute() {
        if (nameTagType == "server_mute") return
        nameTagType = "server_mute"
        refreshNameTag()
    }


    private fun refreshNameTag() {
        val config = getConfig() ?: return
        if (config.enable) {

            val packet = createNameTagPacket(nameTagId, nameTagUUID, player, config)
            val metaDataPacket = createNameTagMetaDataPacket(nameTagId, config)
            val itemPacket = createNameTagItemPacket(nameTagId, config)


            try {
                for (uuid in melodyPlayer.isSendOffer) {
                    val target = Storage.onlinePlayers[uuid] ?: continue
                    if (target.uuid != player.uniqueId.toString()) {
                        if (packet != null) {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(target.player, packet)
                        }
                        if (metaDataPacket != null) {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(target.player, metaDataPacket)
                        }
                        if (itemPacket != null) {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(target.player, itemPacket)
                        }
                    }
                }
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

        } else {
            clearNameTag()
        }
    }

    fun clearNameTag() {
        object : BukkitRunnable() {
            override fun run() {
                val packet = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
                packet.intLists.writeSafely(0, listOf(nameTagId))
                try {
                    for (player in Bukkit.getOnlinePlayers()) {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
                    }
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            }
        }.runTask(MelodyMine.instance)

    }

    fun moveNameTag(from: Location, to: Location) {
        val config = getConfig() ?: return
        val distance = from.distance(to)
        val packet: PacketContainer

        if (distance < 8) {
            packet = PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT)
            packet.integers.write(0, nameTagId)
            packet.doubles
                .writeSafely(0, player.location.x + config.position.x)
                .writeSafely(1, player.location.y + config.position.y)
                .writeSafely(2, player.location.z + config.position.z)
        } else {
            packet = PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE)
            packet.integers.write(0, nameTagId)
            packet.shorts
                .write(0, (((to.x * 32 - from.x * 32) * 128) + config.position.x).toInt().toShort())
                .write(1, (((to.y * 32 - from.y * 32) * 128) + config.position.y).toInt().toShort())
                .write(2, (((to.z * 32 - from.z * 32) * 128) + config.position.z).toInt().toShort())

        }

        try {
            for (uuid in melodyPlayer.isSendOffer) {
                val player = Storage.onlinePlayers[uuid] ?: continue
                ProtocolLibrary.getProtocolManager().sendServerPacket(player.player, packet)
            }
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

    }

    private fun getConfig(): NameTagConfig? {
        return Talk.nameTagConfigs[nameTagType]
    }


}