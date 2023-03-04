package me.past2l.api.scheduler

import me.past2l.api.PluginManager
import me.past2l.api.entity.NPC
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