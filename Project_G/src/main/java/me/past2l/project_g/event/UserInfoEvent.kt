package me.past2l.project_g.event

import me.past2l.project_g.gui.UserInfoGUI
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent

class UserInfoEvent: Listener {
    @EventHandler // Click Interaction TEST
    fun onPlayerInteractByPlayer(event: PlayerInteractAtEntityEvent) {
        if (event.hand.name == "OFF_HAND" || event.rightClicked !is org.bukkit.entity.Player) return
        val player = Bukkit.getPlayer(event.rightClicked.name) ?: return
        if (event.player.isSneaking)
            UserInfoGUI.open(event.player, player)
    }
}