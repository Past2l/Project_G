package me.past2l.project_g.scheduler

import me.past2l.project_g.PluginManager
import me.past2l.project_g.entity.NPC
import org.bukkit.Bukkit

class NPCSkinReloadScheduler {
    companion object {
        private var id: Int? = null

        fun init() {
            id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                PluginManager.plugin,
                { NPC.render() },
                100,
                100
            )
        }

        fun remove() {
            if (id != null)
                Bukkit.getScheduler().cancelTask(id!!)
        }
    }
}