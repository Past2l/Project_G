package me.past2l.api.event

import me.past2l.api.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class GUIEvent: Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked
        val title = event.view.title
        val gui = Player.gui[player.uniqueId]
        if(gui?.title == title) {
            event.isCancelled = !gui!!.canClick(event.rawSlot)
            if(event.clickedInventory == event.view.topInventory)
                gui.getClickEvent(event.rawSlot)?.let { it(event) }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player
        val title = event.view.title
        val gui = Player.gui[player.uniqueId]
        if(gui?.title == title) {
            gui?.getCloseEvent()?.let{ it(event, gui) }
            Player.gui.remove(player.uniqueId)
        }
    }
}