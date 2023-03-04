package me.past2l.minefarm.command

import me.past2l.minefarm.gui.AdminGUI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class AdminCommand: CommandExecutor, TabExecutor {
    companion object {
        const val name = "admin"
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (!sender.isOp) sender.sendMessage("§cPermission Denied.§r")
        else AdminGUI.open(sender)
        return true
    }
}