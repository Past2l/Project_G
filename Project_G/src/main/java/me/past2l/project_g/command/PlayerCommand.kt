package me.past2l.minefarm.command

import me.past2l.api.gui.TabList
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class PlayerCommand: CommandExecutor, TabExecutor {
    companion object {
        const val name = "player"
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        if (sender !is Player || !sender.isOp) return mutableListOf()
        return when (args.size) {
            1 -> mutableListOf("prefix", "like")
            2 -> when (args[0]) {
                in arrayOf("prefix", "like") ->
                    Bukkit.getOnlinePlayers().map { it.name } as MutableList
                else -> mutableListOf()
            }
            else -> mutableListOf()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val help = "§e---------------§r Help: /$name §e---------------§r\n" +
            "§6/$name prefix <nickname> [prefix]§r: 플레이어의 칭호를 설정합니다.\n" +
            "§6/$name like <nickname> <int>§r: 플레이어의 호감도를 설정합니다."
        if (sender !is Player) return false
        if (!sender.isOp) sender.sendMessage("§cPermission Denied.§r")
        else if (args.isEmpty())
            sender.sendMessage(help)
        else when (args[0]) {
            "prefix" -> {
                val nickname = args[1]
                val player = Bukkit.getPlayer(nickname)
                val prefix = args.joinToString(" ")
                    .substring("prefix $nickname".length)
                    .replace("&", "§")
                    .trim()
                if (!Bukkit.getOnlinePlayers().contains(player) || player == null)
                    sender.sendMessage("§c온라인 상태인 플레이어에게서만 칭호를 변경할 수 있습니다.§r")
                else {
                    me.past2l.api.entity.Player.data[player.uniqueId]?.prefix = prefix
                    me.past2l.api.entity.Player.onChangeData(player)
                    TabList.setHeaderFooter()
                    if (prefix.isNotEmpty())
                        sender.sendMessage((if (sender.uniqueId != player.uniqueId) "§a$nickname§r님" else "자신") +
                            "의 칭호를 '$prefix§r'로 변경하였습니다.")
                    else
                        sender.sendMessage((if (sender.uniqueId != player.uniqueId) "§a$nickname§r님" else "자신") +
                            "의 칭호를 제거하였습니다.")
                }
            }
            "like" -> if (args.size != 3)
                sender.sendMessage(
                    "§e---------------§r Help: /$name like §e---------------§r\n" +
                        "§6/$name like <nickname> <int>§r: 플레이어의 호감도를 설정합니다."
                )
            else if (Bukkit.getPlayer(args[1]) == null || !Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1])))
                sender.sendMessage("§c온라인 상태인 플레이어에게서만 호감도를 변경할 수 있습니다.§r")
            else if (args[2].toDoubleOrNull() == null || args[2].indexOf(".") != -1)
                sender.sendMessage("§c정수를 입력해주세요.§r")
            else {
                val player = Bukkit.getPlayer(args[1])!!
                me.past2l.api.entity.Player.data[player.uniqueId]?.like = args[2].toDouble()
                me.past2l.api.entity.Player.onChangeData(player)
                sender.sendMessage((if (sender.uniqueId != player.uniqueId) "§a${args[1]}§r님" else "자신") +
                    "의 호감도를 §d${args[2]}§r로 변경하였습니다.")
            }
            else -> sender.sendMessage(help)
        }
        return true
    }

}