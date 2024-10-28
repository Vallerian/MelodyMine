package ir.taher7.melodymine.listeners

import ir.taher7.melodymine.utils.Utils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.*

class QRCodeListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (player.inventory.firstEmpty() != -1) {
            player.inventory.forEach { itemStack ->
                if (itemStack != null && Utils.isMap(itemStack)) player.inventory.remove(itemStack)
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        if (player.inventory.firstEmpty() != -1) {
            player.inventory.forEach { itemStack ->
                if (itemStack != null && Utils.isMap(itemStack)) player.inventory.remove(itemStack)
            }
        }
    }

    @EventHandler
    fun onInventoryInteract(event: InventoryInteractEvent) {
        if (event.inventory.firstEmpty() != -1) {
            event.inventory.forEach { itemStack ->
                if (itemStack != null && Utils.isMap(itemStack)) event.inventory.remove(itemStack)
            }
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val item = event.currentItem ?: return
        val player = event.whoClicked
        if (player !is Player) return
        if (Utils.isMap(item)) {
            player.inventory.remove(item)
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInventoryMove(event: InventoryMoveItemEvent) {
        val item = event.item
        if (Utils.isMap(item)) {
            event.destination.remove(item)
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        val item = event.itemDrop.itemStack
        val player = event.player
        if (Utils.isMap(item)) {
            player.inventory.remove(item)
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onSwapHand(event: PlayerSwapHandItemsEvent) {
        Utils.removeMap(event.player)
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val item = event.cursor ?: return
        val player = event.view.player
        if (player !is Player) return
        if (Utils.isMap(item)) {
            player.inventory.remove(item)
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInventoryCreative(event: InventoryCreativeEvent) {
        if (event.inventory.firstEmpty() != -1) {
            event.inventory.forEach { itemStack ->
                if (itemStack != null && Utils.isMap(itemStack)) event.inventory.remove(itemStack)
            }
        }
    }

    @EventHandler
    fun onOpenInventory(event: InventoryOpenEvent) {
        if (event.inventory.firstEmpty() != -1) {
            event.inventory.forEach { itemStack ->
                if (itemStack != null && Utils.isMap(itemStack)) event.inventory.remove(itemStack)
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity.player ?: return
        if (player.inventory.firstEmpty() == -1) return
        if (event.drops.any { itemStack -> Utils.isMap(itemStack) }) {
            val item = event.drops.filter { itemStack -> Utils.isMap(itemStack) }[0]
            event.drops.remove(item)
        }
    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        if (event.player.inventory.firstEmpty() == -1) return
        val item = event.player.inventory.itemInMainHand
        if (Utils.isMap(item)) {
            event.player.inventory.remove(item)
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onCommandPreProcess(event: PlayerCommandPreprocessEvent) {
        Utils.removeMap(event.player)
    }

    @EventHandler
    fun onInventory(event: InventoryEvent) {
        if (event.inventory.firstEmpty() != -1) {
            event.inventory.forEach { itemStack ->
                if (itemStack != null && Utils.isMap(itemStack)) event.inventory.remove(itemStack)
            }
        }
    }


    @EventHandler
    fun onInventoryPickupItem(event: InventoryPickupItemEvent) {
        if (event.inventory.firstEmpty() != -1) {
            event.inventory.forEach { itemStack ->
                if (itemStack != null && Utils.isMap(itemStack)) event.inventory.remove(itemStack)
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        Utils.removeMap(event.player)
    }
}
