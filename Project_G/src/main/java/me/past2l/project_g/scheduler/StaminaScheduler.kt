package me.past2l.project_g.scheduler

import me.past2l.project_g.PluginManager
import me.past2l.project_g.entity.Player
import me.past2l.project_g.type.player.PlayerData
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.text.DecimalFormat

class StaminaScheduler {
    companion object {
        private var id: Int? = null

        private val elytra = hashMapOf<org.bukkit.entity.Player, ItemStack>()

        fun init() {
            id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                PluginManager.plugin,
                {
                    Bukkit.getOnlinePlayers().forEach {
                        val maxStamina = PlayerData(it.name, it.uniqueId).stamina
                        val data = Player.data[it.uniqueId]!!

                        if (data.stamina <= 0) {
                            data.stamina = 0F
                            if (it.location.block.isLiquid && it.health > 0)
                                it.health = 0.0
                            else if ((it.isFlying || it.isGliding) && it.equipment.chestplate.type == Material.ELYTRA) {
                                elytra[it] = it.equipment.chestplate
                                it.equipment.chestplate = ItemStack(Material.AIR)
                            }
                        } else if (elytra[it] != null) {
                            it.equipment.chestplate = elytra[it]
                            elytra.remove(it)
                        }

                        data.stamina -= when {
                            it.isSprinting -> if (data.stamina > 0) 18F / 20 else 0F
                            it.isFlying || it.isGliding -> if (data.stamina > 0) 3F / 20 else 0F
                            it.fallDistance > 0 -> 0F
                            it.isSneaking -> -2F
                            it.velocity.length() == 0.0 -> -3F
                            else -> -1F
                        }

                        if (data.stamina > maxStamina)
                            data.stamina = maxStamina

                        val color = when {
                            data.stamina < 60 -> "§c"
                            data.stamina < 120 -> "§e"
                            data.stamina < 180 -> "§a"
                            data.stamina < 240 -> "§b"
                            else -> "§d"
                        }

                        if (data.stamina < maxStamina)
                            it.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                TextComponent("${color}스테미나: " +
                                    "${DecimalFormat("#.#").format(data.stamina)} / ${maxStamina.toInt()}")
                            )
                    }
                },
                1, 1
            )
        }

        fun remove() {
            if (id != null)
                Bukkit.getScheduler().cancelTask(id!!)
        }
    }
}