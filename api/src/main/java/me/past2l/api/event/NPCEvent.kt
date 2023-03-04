package me.past2l.api.event

import me.past2l.api.entity.NPC
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class NPCEvent: Listener {
    @EventHandler
    fun onJoined(event: PlayerJoinEvent) {
        NPC.spawn(event.player)
    }
}