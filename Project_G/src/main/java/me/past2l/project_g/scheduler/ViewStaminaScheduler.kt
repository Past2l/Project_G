package me.past2l.project_g.scheduler

import me.past2l.project_g.PluginManager
import me.past2l.project_g.entity.Player
import org.bukkit.Bukkit

class ViewStaminaScheduler {
    companion object {
        private var id: Int? = null

        fun init() {
            id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                PluginManager.plugin,
                {
                    Bukkit.getOnlinePlayers().forEach {
                        val color: String = when (Player.data[it.uniqueId]?.stamina) {
                            in 0..59 -> "§c"
                            in 60..119 -> "§e"
                            in 120..179 -> "§a"
                            in 180..239 -> "§b"
                            else -> "§d"
                        }
                        it.spigot().sendMessage(
                            net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                            net.md_5.bungee.api.chat.TextComponent("${color}체력: ${Player.data[it.uniqueId]?.stamina} / 240")
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