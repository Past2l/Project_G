package me.past2l.minefarm.gui

import me.past2l.api.PluginManager
import me.past2l.api.gui.GUI
import me.past2l.api.util.Item
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.math.ceil

class AdminGUI {
    companion object {
        private fun userList(player: Player, page: Int = 1) {
            val gui = GUI("Admin GUI", 5)
            val players = Bukkit.getOnlinePlayers().toMutableList()
            val maxPage = ceil(players.size / 27.0).toInt()
            for (idx in 0 until 27) {
                val pageIdx = (page - 1) * 27 + idx
                if (pageIdx >= players.size) break
                gui.setItem(idx, Item.playerHead(players[pageIdx])
                        .setName((if (players[pageIdx].isOp) "§r§a[§cOP§a]§r " else "")
                            + "§r§e§l${players[pageIdx].name}§r§f").item) {
                    userDetail(player, players[pageIdx], page)
                }
            }
            (0 until 9).forEach {
                gui.setItem(
                    it + 27,
                    Item(Material.STAINED_GLASS_PANE, 1, 8)
                        .setName(" ")
                        .glow().item
                )
            }
            gui.setItem(40, Item(Material.BOOK).setName("§r§lPage $page / $maxPage").item)
            if (page > 1)
                gui.setItem(37, Item(Material.ARROW).setName("§r§lPrevious Page").item)
                { userList(player, page - 1) }
            if (page < maxPage)
                gui.setItem(43, Item(Material.ARROW).setName("§r§lNext Page").item)
                { userList(player, page + 1) }
            gui.open(player)
        }

        private fun userDetail(player: Player, target: Player, currentPage: Int = 1) {
            val gui = GUI(
                (if (target.isOp) "§r§a[§cOP§a]§r " else "")
                    + target.displayName,
                1,
                closeEvent = { _, _ ->
                    Bukkit.getScheduler().runTask(
                        PluginManager.plugin
                    ) { userList(player, currentPage) }
                }
            )
            gui.setItem(
                2,
                Item(Material.ENDER_PEARL)
                    .setName("§r§lTeleport§r")
                    .item
            ) {
                gui.close(player)
                player.teleport(target.location)
                player.sendMessage("Teleported to §a${target.name}§r.")
            }
            gui.setItem(
                3,
                Item(Material.CHEST)
                    .setName("§r§lShow Inventory§r")
                    .item
            ) { userInventory(player, target) }
            gui.setItem(
                4,
                Item(Material.ENDER_CHEST)
                    .setName("§r§lShow Ender Chest§r")
                    .item
            ) { userEnderChest(player, target) }
            gui.setItem(
                5,
                Item(Material.STAINED_GLASS_PANE, 1, 4)
                    .setName("§r§e§lKick User§r")
                    .item
            ) {
                if (!target.isOp) {
                    gui.close(player)
                    target.kickPlayer("Kicked by an operator")
                    player.sendMessage("You kicked §a${target.name}§r.")
                } else
                    player.sendMessage("§cYou can't kick ${target.name} because ${target.name} is OP.")
            }
            gui.setItem(
                6,
                Item(Material.BARRIER)
                    .setName("§r§c§lBan User§r")
                    .item
            ) {
                if (!target.isOp) {
                    gui.close(player)
                    me.past2l.api.entity.Player.banPlayer(target)
                    player.sendMessage("You banned §a${target.name}§r.")
                } else
                    player.sendMessage("§cYou can't ban ${target.name} because ${target.name} is OP.")
            }
            gui.open(player)
        }

        private fun userInventory(player: Player, target: Player) {
            val gui = GUI(
                (if (target.isOp) "§r§a[§cOP§a]§r " else "")
                    + "${target.displayName}'s Inventory", 5,
                closeEvent = { _, _ ->
                    Bukkit.getScheduler().runTask(
                        PluginManager.plugin
                    ) { userDetail(player, target) }
                }
            )
            (0 until 36).forEach {
                val item = target.inventory.getItem(it)
                if (item != null)
                    gui.setItem(if (it < 9) 27 + it else it - 9, item) { e ->
                        Item.giveItemAmount(e.whoClicked as Player, item, item.amount)
                    }
            }
            hashMapOf(
                37 to target.inventory.armorContents[3],
                38 to target.inventory.armorContents[2],
                39 to target.inventory.armorContents[1],
                40 to target.inventory.armorContents[0],
                42 to target.inventory.itemInMainHand,
                43 to target.inventory.itemInOffHand,
            ).forEach {
                if (it.value != null)
                    gui.setItem(it.key, it.value!!) { e ->
                        Item.giveItemAmount(e.whoClicked as Player, it.value!!, it.value!!.amount)
                    }
            }
            gui.open(player)
        }

        private fun userEnderChest(player: Player, target: Player) {
            val gui = GUI(
                (if (target.isOp) "§r§a[§cOP§a]§r " else "")
                    + "${target.displayName}'s Ender Chest", 3,
                closeEvent = { _, _ ->
                    Bukkit.getScheduler().runTask(
                        PluginManager.plugin
                    ) { userDetail(player, target) }
                }
            )
            (0 until 27).forEach {
                val item = target.enderChest.getItem(it)
                if (item != null)
                    gui.setItem(it, item) { e ->
                        Item.giveItemAmount(e.whoClicked as Player, item, item.amount)
                    }
            }
            gui.open(player)
        }

        fun open(player: Player) {
            userList(player)
        }
    }
}