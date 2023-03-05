package me.past2l.project_g.scheduler

import me.past2l.project_g.PluginManager
import me.past2l.project_g.gui.Scoreboard
import me.past2l.project_g.gui.TabList
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