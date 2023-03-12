package me.past2l.project_g.event

import me.past2l.project_g.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSprintEvent

class StaminaEvent: Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val data = Player.data[event.player.uniqueId]!!
        if (player.location.block.isLiquid) {
            val from = event.from.toVector()
            val to = event.to.toVector()
            val distance = to.subtract(from).length() * 4
            data.stamina -= distance.toFloat()
        }
    }

    @EventHandler
    fun onSprinting(event: PlayerToggleSprintEvent) {
        val data = Player.data[event.player.uniqueId]!!
        if (data.stamina <= 0)
            event.isCancelled = true
    }
}