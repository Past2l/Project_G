package me.past2l.api.event

import me.past2l.api.util.Config
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class MOTDEvent: Listener {
    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        event.motd = if (!Config.motd.center)
            Config.format(Config.motd.content.joinToString("\n"), trim = false)
        else {
            Config.motd.content.joinToString("\n") {
                val content = Regex("(?i)§[0-9A-FK-OR]").replace(Config.format(it, trim = false), "")
                val length = 30 - (content.length - 1) / 2
                (if (length > 0) " ".repeat(length) else "") + Config.format(it, trim = false)
            }
        }
    }
}