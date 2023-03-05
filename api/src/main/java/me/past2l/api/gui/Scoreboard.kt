package me.past2l.api.gui

import me.past2l.api.config.Config
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot

class Scoreboard {
    companion object {
        private val manager = Bukkit.getScoreboardManager()

        fun set(player: Player) {
            if (!Config.enable.scoreboard) return
            val scoreboard = manager?.newScoreboard
            val objective = scoreboard?.registerNewObjective(
                Config.format(Config.scoreboard.title),
                "dummy"
            )
            objective?.displaySlot = DisplaySlot.SIDEBAR
            val text = Config.scoreboard.content.reversed()
            for (idx in text.indices)
                objective?.getScore(Config.format(text[idx], player, trim = false))?.score = idx
            if (scoreboard != null)
                player.scoreboard = scoreboard
        }
    }
}