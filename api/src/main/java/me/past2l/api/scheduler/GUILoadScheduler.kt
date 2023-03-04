package me.past2l.api.scheduler

import me.past2l.api.PluginManager
import me.past2l.api.gui.Scoreboard
import me.past2l.api.gui.TabList
import org.bukkit.Bukkit

class GUILoadScheduler {
    companion object {
        private var id: Int? = null

        fun init() {
            id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                PluginManager.plugin,
                {
                    TabList.setHeaderFooter()
                    TabList.setPlayerName()
                    Bukkit.getOnlinePlayers().forEach { Scoreboard.set(it) }
                },
                0,
                600
            )
        }

        fun remove() {
            if (id != null)
                Bukkit.getScheduler().cancelTask(id!!)
        }
    }
}