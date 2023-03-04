package me.past2l.minefarm.command

import me.past2l.minefarm.entity.CustomNPC
import me.past2l.api.type.entity.NPCData
import me.past2l.api.type.interact.Interaction
import me.past2l.api.util.Config
import me.past2l.api.util.Logger
import me.past2l.api.util.Skin
import me.past2l.minefarm.gui.CustomGUI
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class CustomNPCCommand: CommandExecutor, TabExecutor {
    companion object {
        const val name = "npc"
    }

    private val help = hashMapOf(
        "default" to "§e---------------§r Help: /$name §e---------------§r\n" +
            "§6/$name create <npc_id> [npc_name]§r: NPC를 생성합니다.\n" +
            "§6/$name remove <npc_id>§r: NPC를 제거합니다.\n" +
            "§6/$name set <npc_id> <key> <value>:§r NPC의 정보를 설정합니다.\n" +
            "§6/$name info <npc_id>:§r NPC 정보를 확인합니다.",
        "set" to "§e---------------§r Help: /$name set §e---------------§r\n" +
            "§6/$name set <npc_id> name <name>:§r NPC의 이름을 설정합니다.\n" +
            "§6/$name set <npc_id> skin <nickname>:§r NPC의 스킨을 설정합니다.\n" +
            "§6/$name set <npc_id> text <text>:§r NPC가 상호작용을 하면 생성할 텍스트를 설정합니다.\n" +
            "§6/$name set <npc_id> gui <gui_id>:§r NPC와 상호작용을 하면 생성할 GUI를 설정합니다."
    )

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        if (sender !is Player) return mutableListOf()
        return when(args.size) {
            1 -> mutableListOf("create", "remove", "set", "info")
            2 -> when (args[0]) {
                in arrayOf("remove", "info", "set") ->
                    CustomNPC.config.values.map { it.id } as MutableList
                else -> mutableListOf()
            }
            3 -> when (args[0]) {
                "set" -> mutableListOf("name", "skin", "gui", "text")
                else -> mutableListOf()
            }
            4 -> when (args[0]) {
                "set" -> when (args[2]) {
                    "skin" -> Bukkit.getOnlinePlayers().map { it.name } as MutableList
                    else -> mutableListOf()
                }
                else -> mutableListOf()
            }
            else -> mutableListOf()
        }
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return false
        if (args.isEmpty()) sender.sendMessage(help["default"])
        else when (args[0]) {
            "create" -> {
                if (args.size < 2) sender.sendMessage(help["default"])
                else if (CustomNPC.npcs[args[1]] != null)
                    sender.sendMessage("§c이미 다른 NPC가 이 ID를 사용하고 있습니다.§r")
                else {
                    val name = args.joinToString(" ")
                        .substring("create ${args[1]}".length).trim()
                    if (Config.format(name).length > 16)
                        sender.sendMessage("§c이름이 너무 깁니다.§r")
                    val npc = NPCData(
                        id = args[1],
                        name = if (args.size > 2) Config.format(name) else args[1],
                        location = sender.location,
                        skin = Skin.getByNickname(if (args.size > 2) name else args[1])
                    )
                    CustomNPC.spawn(npc)
                    CustomNPC.saveData(npc = npc)
                    Logger.log("&a${sender.name}&r 플레이어가 &e${npc.name}&r " +
                        "&6(ID : ${npc.id})&r NPC를 &a생성&r하였습니다.")
                    sender.sendMessage("§a${npc.name}§r §8(ID : ${npc.id})§r NPC가 생성되었습니다.")
                }
            }
            "remove" -> {
                if (args.size != 2) sender.sendMessage(help["default"])
                else if (CustomNPC.config[args[1]] == null)
                    sender.sendMessage("§c존재하지 않는 NPC입니다.§r")
                else {
                    val npc = CustomNPC.config[args[1]]!!
                    Logger.log("&a${sender.name}&r 플레이어가 &e${npc.name}&r " +
                        "&6(ID : ${npc.id})&r NPC를 &c제거&r하였습니다.")
                    sender.sendMessage("§c${npc.name}§r §8(ID : ${npc.id})§r NPC가 제거되었습니다.")
                    CustomNPC.remove(args[1])
                }
            }
            "info" -> {
                if (args.size != 2) sender.sendMessage(help["default"])
                else if (CustomNPC.config[args[1]] == null)
                    sender.sendMessage("§c존재하지 않는 NPC입니다.§r")
                else {
                    val npc = CustomNPC.config[args[1]]!!
                    sender.sendMessage(
                        "§e---------------§r NPC 정보 §e---------------§r\n" +
                            "ID : §6${npc.id}§r\n" +
                            "Entity ID : §6${CustomNPC.npcs[npc.id]?.id}§r\n" +
                            "이름 : §6${npc.name}§r\n" +
                            "UUID : §6${npc.uuid}§r\n" +
                            "월드 : §6${npc.location.world.name}§r\n" +
                            "위치 : §6${npc.location.x}§r / §6${npc.location.y}§r / §6${npc.location.z}§r\n" +
                            "yaw : §6${npc.location.yaw}§r, pitch : §6${npc.location.pitch}§r\n" +
                            "상호작용 타입 : §6${npc.interaction?.type ?: "null"}§r\n" +
                            "상호작용 ID : §6${npc.interaction?.id ?: "null"}§r"
                    )
                }
            }
            "set" -> {
                if (args.size < 4) sender.sendMessage(help["set"])
                else if (CustomNPC.config[args[1]] == null)
                    sender.sendMessage("§c존재하지 않는 NPC입니다.§r")
                else {
                    val data = CustomNPC.config[args[1]]!!
                    when (args[2]) {
                        "name" -> {
                            val name = Config.format(args.joinToString(" ")
                                .substring("set ${args[1]} name".length).trim())
                            if (data.name == name)
                                sender.sendMessage("§c같은 이름으로 바꿀 수 없습니다.§r")
                            else if (name.length > 16)
                                sender.sendMessage("§c이름이 너무 깁니다.§r")
                            else {
                                Logger.log("&a${sender.name}&r 플레이어가 &e${data.name}&r " +
                                    "&6(ID : ${data.id})&r NPC의 이름을 변경하였습니다.")
                                Logger.log("전 닉네임 : &e${data.name}&r, 현재 닉네임 : &e$name&r")
                                sender.sendMessage("§c${data.name}§r §8(ID : ${data.id})§r NPC의 이름이 §a$name§r으로 변경되었습니다.")
                                data.name = name
                                CustomNPC.reload(data)
                            }
                        }
                        "skin" -> {
                            data.skin = Skin.getByNickname(args[3])
                            CustomNPC.reload(data)
                            Logger.log("&a${sender.name}&r 플레이어가 &e${data.name}&r " +
                                "&6(ID : ${data.id})&r NPC의 스킨을 변경하였습니다.")
                            Logger.log("스킨 출처 : &e${args[3]}&r")
                            Logger.log("스킨 사진 : &e${data.skin.skin}&r")
                            Logger.log("망토 사진 : &e${data.skin.cape}&r")
                            sender.sendMessage("§a${data.name}§r §8(ID : ${data.id})§r NPC의 스킨이 변경되었습니다.")
                        }
                        "gui" -> {
                            if (CustomGUI.data[args[3]] == null)
                                sender.sendMessage("§c존재하지 않는 GUI ID입니다.§r")
                            else {
                                data.interaction = Interaction("gui", args[3])
                                CustomNPC.reload(data)
                                Logger.log("&a${sender.name}&r 플레이어가 &e${data.name}&r " +
                                    "&6(ID : ${data.id})&r NPC의 상호작용을 변경하였습니다.")
                                Logger.log("상호작용 타입 : &egui&r, 상호작용 ID : &e${args[3]}&r")
                                sender.sendMessage("§a${data.name}§r §8(ID : ${data.id})§r NPC의 상호작용이 변경되었습니다.")
                            }
                        }
                        "text" -> {
                            val text = args.joinToString(" ")
                                .substring("set ${args[1]} text".length).trim()
                            data.interaction = Interaction("text", text)
                            CustomNPC.reload(data)
                            Logger.log("&a${sender.name}&r 플레이어가 &e${data.name}&r " +
                                "&6(ID : ${data.id})&r NPC의 상호작용을 변경하였습니다.")
                            Logger.log("상호작용 타입 : &etext&r, 상호작용 ID : &e'&r$text&e'&r")
                            sender.sendMessage("§a${data.name}§r §8(ID : ${data.id})§r NPC의 상호작용이 변경되었습니다.")
                        }
                        else -> sender.sendMessage(help["set"])
                    }
                }
            }
            else -> sender.sendMessage(help["default"])
        }
        return true
    }
}