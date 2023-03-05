package me.past2l.project_g

import org.bukkit.plugin.java.JavaPlugin

class PluginManager {
    companion object {
        lateinit var plugin: JavaPlugin

        fun init(plugin: JavaPlugin) {
            Companion.plugin = plugin
        }
    }
}