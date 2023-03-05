package me.past2l.project_g.scheduler

import me.past2l.project_g.PluginManager
import me.past2l.project_g.entity.Player
import org.bukkit.Bukkit

class StaminaScheduler {
    companion object {
        private var id: Int? = null

        fun init() {
            id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                PluginManager.plugin,
                {
                    Bukkit.getOnlinePlayers().forEach {
                        val maxStamina = 240
                        if (Player.data[it.uniqueId]?.stamina!! < 0)
                            Player.data[it.uniqueId]?.stamina = 0
                        else if (Player.data[it.uniqueId]?.stamina!! > maxStamina)
                            Player.data[it.uniqueId]?.stamina = maxStamina

                        if (it.isSprinting || it.isFlying || it.location.block.isLiquid) {
                            if (Player.data[it.uniqueId]?.stamina!! > 0)
                                Player.data[it.uniqueId]?.stamina = Player.data[it.uniqueId]?.stamina!! - 1
                        } else if (it.isSneaking) {
                            if (Player.data[it.uniqueId]?.stamina!! < maxStamina)
                                Player.data[it.uniqueId]?.stamina = Player.data[it.uniqueId]?.stamina!! + 2
                        } else if (it.velocity.length() == 0.0) {
                            if (Player.data[it.uniqueId]?.stamina!! < maxStamina)
                                Player.data[it.uniqueId]?.stamina = Player.data[it.uniqueId]?.stamina!! + 3
                        } else {
                            if (Player.data[it.uniqueId]?.stamina!! < maxStamina)
                                Player.data[it.uniqueId]?.stamina = Player.data[it.uniqueId]?.stamina!! + 1
                        }

                        if (Player.data[it.uniqueId]?.stamina!! >= maxStamina) {
                            it.spigot().sendMessage(
                                net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                                net.md_5.bungee.api.chat.TextComponent("")
                            )
                            return@forEach
                        }

                        val color: String = when (Player.data[it.uniqueId]?.stamina) {
                            in 0..59 -> "§c"
                            in 60..119 -> "§e"
                            in 120..179 -> "§a"
                            in 180..239 -> "§b"
                            else -> "§d"
                        }
                        it.spigot().sendMessage(
                            net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                            net.md_5.bungee.api.chat.TextComponent("${color}체력: ${Player.data[it.uniqueId]?.stamina} / $maxStamina")
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