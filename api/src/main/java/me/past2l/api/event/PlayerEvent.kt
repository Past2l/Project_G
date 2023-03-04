package me.past2l.api.event

import me.past2l.api.PluginManager
import me.past2l.api.entity.Player
import me.past2l.api.gui.Scoreboard
import me.past2l.api.gui.TabList
import me.past2l.api.util.Config
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.time.ZoneId
import java.time.ZonedDateTime

class PlayerEvent: Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val data = Player.loadData(event.player)
        data.lastPlayed = ZonedDateTime.now(ZoneId.of(Config.timezone))
        Player.data[event.player.uniqueId] = data
        TabList.setHeaderFooter()
        TabList.setPlayerName()
        Scoreboard.set(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val data = Player.data[event.player.uniqueId]!!
        val now = ZonedDateTime.now(ZoneId.of(Config.timezone))
        data.playtime += (now.toInstant().epochSecond - data.lastPlayed.toInstant().epochSecond) / 3600.0
        data.lastPlayed = now
        Player.saveData(event.player)
        Player.data.remove(event.player.uniqueId)
        Bukkit.getScheduler().runTaskLater(
            PluginManager.plugin,
            { TabList.setHeaderFooter() },
            20,
        )
    }

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        if (Config.enable.chat)
            event.format = Config.format(Config.chat, event.player)
                .replace("%chat.message%", event.message)
                .replace("%", "%%")
    }
}