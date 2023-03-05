package me.past2l.project_g.event

import me.past2l.project_g.entity.NPC
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class NPCEvent: Listener {
    @EventHandler
    fun onJoined(event: PlayerJoinEvent) {
        NPC.spawn(event.player)
    }
}