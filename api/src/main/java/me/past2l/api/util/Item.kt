package me.past2l.api.util

import java.io.*
import java.util.*
import org.bukkit.util.io.*
import org.bukkit.inventory.*
import org.bukkit.inventory.meta.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.enchantments.Enchantment

class Item @JvmOverloads constructor(
    material: Material = Material.STONE,
    amount: Int = 1,
    damage: Short = 0,
    itemStack: ItemStack? = null,
) {
    val item: ItemStack

    companion object {
        fun serialize(item: ItemStack): String {
            val i = ByteArrayOutputStream()
            val os = BukkitObjectOutputStream(i)
            os.writeObject(item)
            os.flush()
            return Base64.getEncoder().encodeToString(i.toByteArray())
        }

        fun deserialize(data: String): Item {
            val o = Base64.getDecoder().decode(data)
            val i = ByteArrayInputStream(o)
            val s = BukkitObjectInputStream(i)
            return Item(itemStack = s.readObject() as ItemStack)
        }

        fun playerHead(player: Player): Item {
            val item = Item(Material.SKULL_ITEM, 1, 3)
            val itemMeta = item.item.itemMeta as SkullMeta
            itemMeta.owningPlayer = player
            item.item.itemMeta = itemMeta
            return item
        }

        fun getItemAmount(player: Player, item: ItemStack): Int {
            var count = 0
            for (idx in 0 until player.inventory.size) {
                val idxItem = player.inventory.getItem(idx) ?: continue
                if (idxItem.isSimilar(item))
                    count += idxItem.amount
            }
            return count
        }

        fun getItemAmount(player: Player, item: Material): Int {
            var count = 0
            for (idx in 0 until player.inventory.size) {
                val idxItem = player.inventory.getItem(idx) ?: continue
                if (idxItem.type == item)
                    count += idxItem.amount
            }
            return count
        }

        fun removeItemAmount(player: Player, item: ItemStack, amount: Int = 1) {
            var left = amount
            for (idx in 0 until player.inventory.size) {
                val idxItem = player.inventory.getItem(idx) ?: continue
                if (idxItem.isSimilar(item))
                    if (idxItem.amount > left) {
                        idxItem.amount -= left
                        left = 0
                    } else {
                        left -= idxItem.amount
                        player.inventory.setItem(idx, null)
                    }
                if (left < 1) break
            }
        }

        fun removeItemAmount(player: Player, item: Material, amount: Int = 1) {
            var left = amount
            for (idx in 0 until player.inventory.size) {
                val idxItem = player.inventory.getItem(idx) ?: continue
                if (idxItem.type == item)
                    if (idxItem.amount > left) {
                        idxItem.amount -= left
                        left = 0
                    } else {
                        left -= idxItem.amount
                        player.inventory.setItem(idx, null)
                    }
                if (left < 1) break
            }
        }

        fun giveItemAmount(player: Player, item: Material, amount: Int = 1) {
            for (i in 0 until amount)
                player.inventory.addItem(ItemStack(item))
        }

        fun giveItemAmount(player: Player, item: ItemStack, amount: Int = 1) {
            val old = item.amount
            item.amount = 1
            for (i in 0 until amount)
                player.inventory.addItem(item)
            item.amount = old
        }

        fun canGiveItem(player: Player, item: ItemStack, amount: Int): Boolean {
            for (idx in 0 until 36) {
                val idxItem = player.inventory.getItem(idx) ?: return true
                if (idxItem.isSimilar(item))
                    if (idxItem.maxStackSize >= idxItem.amount + amount)
                        return true
            }
            return false
        }

        fun canGiveItem(player: Player, item: Material, amount: Int): Boolean {
            for (idx in 0 until 36) {
                val idxItem = player.inventory.getItem(idx) ?: return true
                if (idxItem.type == item)
                    if (idxItem.maxStackSize >= idxItem.amount + amount)
                        return true
            }
            return false
        }
    }

    init {
        item = itemStack ?: ItemStack(material, amount, damage)
    }

    fun setName(name: String): Item {
        val itemMeta = item.itemMeta
        itemMeta?.displayName = name
        item.itemMeta = itemMeta
        return this
    }

    fun setLore(lore: MutableList<String>): Item {
        val itemMeta = item.itemMeta
        itemMeta?.lore = lore
        item.itemMeta = itemMeta
        return this
    }

    fun setEnchantment(
        enchantment: Enchantment,
        level: Int
    ): Item {
        val itemMeta = item.itemMeta
        itemMeta?.addEnchant(enchantment, level, true)
        item.itemMeta = itemMeta
        return this
    }

    fun setEnchantments(enchantments: Map<Enchantment, Int>): Item {
        val itemMeta = item.itemMeta
        for(e in enchantments) itemMeta?.addEnchant(e.key, e.value, true)
        item.itemMeta = itemMeta
        return this
    }

    fun setItemFlag(flag: ItemFlag): Item {
        val itemMeta = item.itemMeta
        itemMeta?.addItemFlags(flag)
        item.itemMeta = itemMeta
        return this
    }

    fun setItemFlags(flags: List<ItemFlag>): Item {
        val itemMeta = item.itemMeta
        for(e in flags) itemMeta?.addItemFlags(e)
        item.itemMeta = itemMeta
        return this
    }

    fun removeItemFlag(flag: ItemFlag): Item {
        val itemMeta = item.itemMeta
        itemMeta?.addItemFlags(flag)
        item.itemMeta = itemMeta
        return this
    }

    fun removeItemFlag(flags: List<ItemFlag>): Item {
        val itemMeta = item.itemMeta
        for(e in flags) itemMeta?.removeItemFlags(e)
        item.itemMeta = itemMeta
        return this
    }

    fun glow(): Item {
        val itemMeta = item.itemMeta
        itemMeta?.addEnchant(Enchantment.DURABILITY, 1, true)
        itemMeta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.itemMeta = itemMeta
        return this
    }
}