package me.past2l.project_g.command

import me.past2l.project_g.gui.EnderChestGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class EnderChestGUICommand: CommandExecutor, TabExecutor {
    companion object {
        const val name = "ender_chest"
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return false
        EnderChestGUI.open(sender)
        return true
    }
}