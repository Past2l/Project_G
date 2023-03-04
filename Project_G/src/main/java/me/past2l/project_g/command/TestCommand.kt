package me.past2l.minefarm.command

import me.past2l.api.PluginManager
import me.past2l.api.gui.GUI
import me.past2l.api.util.Item
import me.past2l.minefarm.MineFarm
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class TestCommand: CommandExecutor, TabExecutor {
    companion object {
        const val name = "test"
    }

    var stand: ArmorStand? = null
    var cosmetic_id: Int? = null
    
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        return mutableListOf("gacha", "cosmetic", "removeCosmetic")
    }

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return false
        when (args[0]) {
            "gacha" -> {
                val items = arrayOf(
                    Item(Material.SAND),
                    Item(Material.STONE),
                    Item(Material.BEDROCK),
                )
                var id: Int? = null
                val gui = GUI(
                    "test",
                    3,
                    openEvent = { gui, player ->
                        var count = 1
                        items.shuffle()
                        id = Bukkit.getScheduler().runTaskTimer(
                            PluginManager.plugin,
                            {
                                (0..8).forEach {
                                    gui.setItem(it, items[(it + count) % items.size].item)
                                }
                                count++
                            },
                            0, 0
                        ).taskId
                    },
                    closeEvent = { _, _ ->
                        if (id != null)
                            Bukkit.getScheduler().cancelTask(id!!)
                    }
                )
                gui.setItem(22, Item(Material.EMERALD_BLOCK).item) {
                    if (id != null)
                        Bukkit.getScheduler().runTaskLater(
                            PluginManager.plugin,
                            {
                                Bukkit.getScheduler().cancelTask(id!!)
                                id = null
                                val item = gui.getItem(4)!!
                                Item.giveItemAmount(it.whoClicked as Player, item, item.amount)
                            },
                            (0..20).random().toLong()
                        )
                }
                gui.open(sender)
            }
            "cosmetic" -> {
                if (stand == null) {
                    stand = sender.world.spawnEntity(
                        sender.location, EntityType.ARMOR_STAND) as ArmorStand
                    stand!!.isMarker = true
                    stand!!.isInvulnerable = true
                    stand!!.isVisible = false
                    stand!!.setGravity(false)
                    stand!!.helmet = sender.inventory.itemInMainHand
                    cosmetic_id = Bukkit.getScheduler().runTaskTimer(
                        JavaPlugin.getPlugin(MineFarm::class.java),
                        {
                            sender.setPassenger(stand!!)
                            stand!!.teleport(sender.location.add(
                                0.0,
                                if (sender.isSneaking) 1.125 else 1.35,
                                0.0
                            ))
                        },
                        0, 0
                    ).taskId
                }
            }
            "removeCosmetic" -> {
                if (stand != null && cosmetic_id != null) {
                    Bukkit.getScheduler().cancelTask(cosmetic_id!!)
                    stand!!.remove()
                    stand = null
                }
            }
        }
        return true
    }
}