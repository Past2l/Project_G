package me.past2l.api.gui

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class GUI @JvmOverloads constructor(
    val title: String,
    lines: Int,
    private val clickAll: Boolean = false,
    private val click: Array<Array<Boolean>?>? = null,
    private val openEvent: ((GUI, Player) -> Unit)? = null,
    private var closeEvent: ((InventoryCloseEvent, GUI) -> Unit)? = null,
    owner: InventoryHolder? = null
) {
    private val inventory: Inventory
    private val clickEvent = hashMapOf<Int, ((InventoryClickEvent) -> Unit)>()

    init {
        this.inventory = Bukkit.createInventory(owner, lines * 9, title)
    }

    fun getItem(slot: Int): ItemStack? {
        return inventory.getItem(slot)
    }

    fun setItem(slot: Int, item: ItemStack, event: ((InventoryClickEvent) -> Unit)? = null) {
        val meta = item.itemMeta
        if (meta != null) {
            for (flag in meta.itemFlags) meta.addItemFlags(flag)
            item.itemMeta = meta
        }
        inventory.setItem(slot, item)
        if(event != null) setClickEvent(slot, event)
    }

    fun getInventory(): Inventory {
        return inventory
    }

    fun getClickEvent(slot: Int): ((InventoryClickEvent) -> Unit)? {
        return clickEvent[slot]
    }

    fun setClickEvent(slot: Int, event: ((InventoryClickEvent) -> Unit)) {
        clickEvent[slot] = event
    }

    fun getCloseEvent(): ((InventoryCloseEvent, GUI) -> Unit)? {
        return closeEvent
    }

    fun removeCloseEvent() {
        closeEvent = null
    }

    fun canClick(slot: Int): Boolean {
        return if(click == null) clickAll else click[slot/9]!![slot%9]
    }

    fun open(player: Player) {
        me.past2l.api.entity.Player.gui[player.uniqueId] = this
        player.openInventory(inventory)
        this.openEvent?.let { it(this, player) }
    }

    fun close(player: Player, removeEvent: Boolean = true) {
        if (removeEvent) removeCloseEvent()
        player.teleport(player.location)
    }
}