package me.past2l.minefarm.gui

import me.past2l.api.gui.GUI
import org.bukkit.entity.Player

class EnderChestGUI {
    companion object {
        fun open(player: Player) {
            val gui = GUI(
                "엔더 상자",
                3,
                true,
                closeEvent = { _, gui ->
                    (0 until 27).forEach {
                        player.enderChest.setItem(it, gui.getItem(it))
                    }
                }
            )
            (0 until 27).forEach {
                val item = player.enderChest.getItem(it)
                if (item != null)
                    gui.setItem(it, item)
            }
            gui.open(player)
        }
    }
}