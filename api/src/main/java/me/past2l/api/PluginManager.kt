package me.past2l.api

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class PluginManager {
    companion object {
        lateinit var plugin: JavaPlugin

        fun init(plugin: JavaPlugin) {
            this.plugin = plugin
        }

        fun reload() {
            Bukkit.getPluginManager().disablePlugin(plugin)
            Bukkit.getPluginManager().enablePlugin(plugin)
        }
    }
}