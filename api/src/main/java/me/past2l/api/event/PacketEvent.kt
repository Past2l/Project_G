package me.past2l.api.event

import me.past2l.api.nms.NMS
import me.past2l.api.packet.Packet
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PacketEvent: Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        NMS.injectPacket(event.player, Packet.read)
    }

    @EventHandler
    fun onLeft(event: PlayerQuitEvent) {
        NMS.removeInjectPacket(event.player)
    }
}