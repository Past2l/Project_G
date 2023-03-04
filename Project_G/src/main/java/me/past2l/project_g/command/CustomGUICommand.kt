package me.past2l.minefarm.command

import me.past2l.minefarm.gui.CustomGUI
import me.past2l.api.gui.GUI
import me.past2l.minefarm.type.gui.GUIData
import me.past2l.minefarm.type.gui.GUIGachaItem
import me.past2l.minefarm.type.gui.GUIItem
import me.past2l.minefarm.type.gui.GUIShopItem
import me.past2l.api.type.interact.Interaction
import me.past2l.api.util.Config
import me.past2l.api.util.Item
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.text.DecimalFormat

class CustomGUICommand: CommandExecutor, TabExecutor {
    companion object {
        const val name = "gui"
    }

    private val help = hashMapOf(
        "default" to "§e---------------§r Help: /$name §e---------------§r\n" +
            "§6/$name create <template> <gui_id> <line>§r: GUI를 생성합니다.\n" +
            "§6/$name remove <gui_id>§r: GUI를 제거합니다.\n" +
            "§6/$name open <gui_id>§r: GUI를 엽니다.\n" +
            "§6/$name slotId <gui_id>§r: GUI에 있는 아이템의 Slot ID를 확인합니다.\n" +
            "§6/$name info <gui_id> [slot_id]§r: GUI나 Item의 정보를 확인합니다.\n" +
            "§6/$name set <gui_id> <key> <value>§r: GUI의 정보를 설정합니다.\n" +
            "§6/$name set <gui_id> item [slot_id] [key] [value]§r: GUI Item의 정보를 설정합니다.",
        "set" to "§e---------------§r Help: /$name set §e---------------§r\n" +
            "§6/$name set <gui_id> <key> <value>§r: GUI의 정보를 설정합니다.\n" +
            "§6/$name set <gui_id> item [slot_id] [key] [value]§r: GUI Item의 정보를 설정합니다.",
        "setItem" to "§e---------------§r Help: /$name set <gui_id> item §e---------------§r\n" +
            "§6/$name set <gui_id> item§r: GUI Item의 정보를 설정합니다.\n" +
            "§6/$name set <gui_id> item <slot_id> gui <gui_id>§r: \n아이템을 클릭하면 열릴 GUI를 설정합니다.\n" +
            "§6/$name set <gui_id> item <slot_id> text <text>§r: \n아이템을 클릭하면 나올 텍스트를 설정합니다.\n" +
            "§6/$name set <gui_id> item <slot_id> price <int|false>§r: \n아이템의 구매가격을 설정합니다.\n" +
            "§6/$name set <gui_id> item <slot_id> sellPrice <int|false>§r: \n아이템의 판매가격을 설정합니다.\n" +
            "§6/$name set <gui_id> item <slot_id> priceChange <boolean>§r: \n아이템의 시세 변경 여부를 설정합니다.\n"+
            "§6/$name set <gui_id> item <slot_id> minPrice <double>§r: \n아이템의 최소 가격을 설정합니다.\n" +
            "§6/$name set <gui_id> item <slot_id> maxPrice <double>§r: \n아이템의 최대 가격을 설정합니다.",
    )

    private val emptyOptions = mutableListOf("gui", "text")
    private val shopOptions = mutableListOf("price", "sellPrice", "priceChange", "minPrice", "maxPrice", "moneyType")
    private val gachaOptions = mutableListOf("percent")

    private val intOptions = mutableListOf("price", "sellPrice", "percent")
    private val doubleOptions = mutableListOf("minPrice", "maxPrice")

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        if (sender !is Player) return mutableListOf()
        return when (args.size) {
            1 -> mutableListOf("create", "remove", "set", "open", "info", "slotId")
            2 -> when (args[0]) {
                "create" -> CustomGUI.templates
                in arrayOf("remove", "open", "set", "info", "slotId") ->
                    CustomGUI.data.map { it.key } as MutableList
                else -> mutableListOf()
            }
            3 -> when (args[0]) {
                "set" -> mutableListOf("name", "item")
                "info" -> (CustomGUI.data[args[1]]?.items?.map { it.slot.toString() } ?: listOf()) as MutableList
                else -> mutableListOf()
            }
            4 -> when (args[0]) {
                "set" -> when (args[2]) {
                    "item" -> (CustomGUI.data[args[1]]?.items?.map { it.slot.toString() } ?: listOf()) as MutableList
                    else -> mutableListOf()
                }
                else -> mutableListOf()
            }
            5 -> when (args[0]) {
                "set" -> when (args[2]) {
                    "item" -> if (
                        args[3] != "all" && (args[3].toIntOrNull() == null ||
                            CustomGUI.data[args[1]]?.items?.find { it.slot == args[3].toInt() } == null)
                    )
                        mutableListOf()
                    else when (CustomGUI.data[args[1]]!!.type) {
                        "empty" -> emptyOptions
                        "shop" -> shopOptions
                        "gacha" -> gachaOptions
                        else -> mutableListOf()
                    }
                    else -> mutableListOf()
                }
                else -> mutableListOf()
            }
            6 -> when (args[0]) {
                "set" -> if (
                    args[3] != "all" &&
                    CustomGUI.data[args[1]]?.items?.find { it.slot == args[3].toInt() } == null
                )
                    mutableListOf()
                else when (args[4]) {
                    "price" -> mutableListOf("false")
                    "sellPrice" -> mutableListOf("false")
                    "priceChange" -> mutableListOf("true", "false")
                    "moneyType" -> mutableListOf("money", "cash")
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
        else if (args.isEmpty()) sender.sendMessage(help["default"])
        else when (args[0]) {
            "create" -> {
                if (args.size != 4) sender.sendMessage(help["default"])
                else if (args[1] !in CustomGUI.templates)
                    sender.sendMessage("§c알맞지 않은 템플릿입니다.§r")
                else if (args[3].toIntOrNull() == null)
                    sender.sendMessage("§c정수를 입력해주세요.§r")
                else if (args[3].toInt() < 1 || args[3].toInt() > 6)
                    sender.sendMessage("§c값이 너무 작거나 큽니다..§r")
                else {
                    val data = GUIData(args[1], args[2], args[2], arrayListOf(), args[3].toInt())
                    CustomGUI.saveData(data)
                    CustomGUI.create(data)
                    sender.sendMessage("§a${data.id}§r GUI가 생성되었습니다.")
                }
            }
            "open" -> {
                if (args.size != 2) sender.sendMessage(help["default"])
                else if (CustomGUI.data[args[1]] == null)
                    sender.sendMessage("§c존재하지 않는 GUI ID입니다.§r")
                else
                    CustomGUI.open(args[1], sender)
            }
            "remove" -> {
                if (args.size != 2) sender.sendMessage(help["default"])
                else if (CustomGUI.data[args[1]] == null)
                    sender.sendMessage("§c존재하지 않는 GUI ID입니다.§r")
                else {
                    val data = CustomGUI.data[args[1]]!!
                    sender.sendMessage("§a${data.id}§r GUI가 제거되었습니다.")
                    CustomGUI.remove(data)
                }
            }
            "info" -> {
                if (args.size < 2 || args.size > 3) sender.sendMessage(help["default"])
                else if (CustomGUI.data[args[1]] == null)
                    sender.sendMessage("§c존재하지 않는 GUI ID입니다.§r")
                else if (args.size == 2) {
                    val gui = CustomGUI.data[args[1]]!!
                    sender.sendMessage(
                        "§e---------------§r GUI 정보 §e---------------§r\n" +
                            "GUI 타입 : §6${gui.type}§r\n" +
                            "GUI ID : §6${gui.id}§r\n" +
                            "GUI 이름 : §6${gui.name}§r\n"+
                            "GUI 줄 : §6${gui.line}§r"
                    )
                } else if (CustomGUI.data[args[1]]?.items?.find { it.slot == args[2].toInt() } == null)
                    sender.sendMessage("§c이 슬롯에 아이템이 존재하지 않습니다.§r")
                else {
                    val data = CustomGUI.data.values.find { it.id == args[1] }!!
                    val idx = data.items.indices.find { i -> data.items[i].slot == args[2].toInt() }!!
                    val item = data.items[idx]
                    val itemStack = Item.deserialize(item.item).item
                    sender.sendMessage(
                        "§e---------------§r GUI Item 정보 §e---------------§r\n" +
                            "Slot ID : §6${args[2]}§r\n" +
                            "아이템 타입 : §6${itemStack.type}§r\n" +
                            "아이템 이름 : §6${itemStack.itemMeta?.displayName ?: "null"}§r" +
                            when (data.type) {
                                "empty" -> "\n" +
                                    "상호작용 타입 : §6${item.interaction?.type ?: "null"}§r\n" +
                                    "상호작용 ID : §6${item.interaction?.id ?: "null"}§r\n"
                                "shop" -> "\n" +
                                    "구매 가격 : §6${item.shop?.price ?: "null"}§r\n" +
                                    "판매 가격 : §6${item.shop?.sellPrice ?: "null"}§r\n" +
                                    "가격 변동 여부 : §6${item.shop?.priceChange ?: "null"}§r\n" +
                                    "이전 구매 가격 : §6${item.shop?.previousPrice ?: "null"}§r\n" +
                                    "이전 판매 가격 : §6${item.shop?.previousSellPrice ?: "null"}§r\n" +
                                    "최소 가격 (배수) : §6${item.shop?.minPrice ?: "null"}§r\n" +
                                    "최대 가격 (배수) : §6${item.shop?.maxPrice ?: "null"}§r"
                                else -> ""
                            }
                    )
                }
            }
            "slotId" -> {
                if (args.size != 2) sender.sendMessage(help["default"])
                else if (CustomGUI.data[args[1]] == null)
                    sender.sendMessage("§c존재하지 않는 GUI ID입니다.§r")
                else {
                    val data = CustomGUI.data[args[1]]!!
                    val gui = GUI("${data.name}§r GUI Items Slot ID", data.line)
                    data.items.forEach { item ->
                        gui.setItem(item.slot, Item.deserialize(item.item).item) {
                            gui.close(it.whoClicked as Player)
                            val suggest = TextComponent("이 텍스트를 누르면 아이템의 값을 설정할 수 있습니다.")
                            suggest.isUnderlined = true
                            suggest.color = ChatColor.YELLOW
                            suggest.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("명령어를 자동완성하려면 클릭해주세요.").create())
                            suggest.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/gui set ${args[1]} item ${item.slot} ")
                            it.whoClicked.sendMessage("이 아이템의 Slot ID는 §6${item.slot}§r입니다.")
                            it.whoClicked.spigot().sendMessage(suggest)
                        }
                    }
                    gui.open(sender)
                }
            }
            "set" -> {
                if (args.size < 3) sender.sendMessage(help["set"])
                else if (CustomGUI.data[args[1]] == null)
                    sender.sendMessage("§c존재하지 않는 GUI ID입니다.§r")
                else {
                    val data = CustomGUI.data[args[1]]!!
                    when (args[2]) {
                        "name" -> {
                            val name = Config.format(args.joinToString(" ")
                                .substring("set ${args[1]} name ".length).trim())
                            data.name = name
                            CustomGUI.reload()
                            sender.sendMessage("§a${args[1]}§r GUI의 이름을 '$name§r'으로 수정하였습니다.")
                        }
                        "item" -> {
                            if (args.size == 3) {
                                val gui = GUI(
                                    "${data.name}§r GUI Items",
                                    data.line, true,
                                    closeEvent = { event, gui ->
                                        val oldItems = data.items
                                        val newItems = arrayListOf<GUIItem>()
                                        for (idx in 0 until data.line * 9)
                                            if (gui.getItem(idx) != null)
                                                newItems.add(
                                                    GUIItem(
                                                    Item.serialize(gui.getItem(idx)!!),
                                                    idx,
                                                    oldItems.find { it.slot == idx }?.interaction,
                                                    oldItems.find { it.slot == idx }?.shop,
                                                    oldItems.find { it.slot == idx }?.gacha,
                                                )
                                                )
                                        data.items = newItems
                                        CustomGUI.reload(data)
                                        event.player.sendMessage("§a${args[1]}§r GUI의 아이템이 수정되었습니다.")
                                    }
                                )
                                data.items.forEach { gui.setItem(it.slot, Item.deserialize(it.item).item) }
                                gui.open(sender)
                            } else if (args.size < 6) sender.sendMessage(help["setItem"])
                            else if (args[3] != "all" && data.items.find { it.slot == args[3].toInt() } == null)
                                sender.sendMessage("§c이 슬롯에 아이템이 존재하지 않습니다.§r")
                            else if (args[4] !in emptyOptions && args[4] !in shopOptions && args[4] !in gachaOptions)
                                sender.sendMessage(help["setItem"])
                            else if (
                                (args[4] in emptyOptions && data.type != "empty") ||
                                (args[4] in shopOptions && data.type != "shop") ||
                                (args[4] in gachaOptions && data.type != "gacha")
                            )
                                sender.sendMessage("§cGUI type이 알맞지 않습니다.§r")
                            else if (
                                (args[4] in doubleOptions && args[5] != "false" && args[5].toDoubleOrNull() == null) ||
                                (args[4] in intOptions && args[5] != "false" &&
                                    (args[5].toDoubleOrNull() == null || args[5].indexOf(".") != -1))
                            )
                                sender.sendMessage("§c값이 알맞지 않습니다.§r")
                            else {
                                if (args[3] != "all") {
                                    val idx = data.items.indices.find { i -> data.items[i].slot == args[3].toInt() }!!
                                    when (data.type) {
                                        "empty" -> if (data.items[idx].interaction == null)
                                            data.items[idx].interaction = Interaction("")
                                        "shop" -> if (data.items[idx].shop == null)
                                            data.items[idx].shop = GUIShopItem(null, null)
                                        "gacha" -> if (data.items[idx].gacha == null)
                                            data.items[idx].gacha = GUIGachaItem()
                                    }
                                    when (args[4]) {
                                        "gui" -> {
                                            data.items[idx].interaction!!.type = "gui"
                                            data.items[idx].interaction!!.id = args[5]
                                        }
                                        "text" -> {
                                            data.items[idx].interaction!!.type = "text"
                                            data.items[idx].interaction!!.id =
                                                Config.format(args.joinToString(" ")
                                                    .substring("set ${args[1]} item ${args[3]} text".length).trim())
                                        }
                                        "price" -> data.items[idx].shop!!.price =
                                            if (args[5] == "false") null else args[5].toDouble()
                                        "sellPrice" -> data.items[idx].shop!!.sellPrice =
                                            if (args[5] == "false") null else args[5].toDouble()
                                        "minPrice" -> data.items[idx].shop!!.minPrice =
                                            if (args[5] == "false") 0.6 else args[5].toDouble()
                                        "maxPrice" -> data.items[idx].shop!!.maxPrice =
                                            if (args[5] == "false") 1.4 else args[5].toDouble()
                                        "priceChange" -> {
                                            data.items[idx].shop!!.priceChange =
                                                args[5] == "true"
                                        }
                                        "moneyType" -> data.items[idx].shop!!.moneyType = args[5]
                                        "percent" -> data.items[idx].gacha!!.percent =
                                            if (args[5] == "false") 10 else args[5].toInt()
                                    }
                                } else
                                    data.items.forEach {
                                        when (data.type) {
                                            "empty" -> if (it.interaction == null)
                                                it.interaction = Interaction()
                                            "shop" -> if (it.shop == null)
                                                it.shop = GUIShopItem()
                                            "gacha" -> if (it.gacha == null)
                                                it.gacha = GUIGachaItem()
                                        }
                                        when (args[4]) {
                                            "gui" -> {
                                                it.interaction!!.type = "gui"
                                                it.interaction!!.id = args[5]
                                            }
                                            "text" -> {
                                                it.interaction!!.type = "text"
                                                it.interaction!!.id =
                                                    Config.format(args.joinToString(" ")
                                                        .substring("set ${args[1]} item ${args[3]} text".length).trim())
                                            }
                                            "price" -> it.shop!!.price =
                                                if (args[5] == "false") null else args[5].toDouble()
                                            "sellPrice" -> it.shop!!.sellPrice =
                                                if (args[5] == "false") null else args[5].toDouble()
                                            "minPrice" -> it.shop!!.minPrice =
                                                if (args[5] == "false") 0.6 else args[5].toDouble()
                                            "maxPrice" -> it.shop!!.maxPrice =
                                                if (args[5] == "false") 1.4 else args[5].toDouble()
                                            "priceChange" -> {
                                                it.shop!!.priceChange =
                                                    args[5] == "true"
                                            }
                                            "moneyType" -> it.shop!!.moneyType = args[5]
                                            "percent" -> it.gacha!!.percent =
                                                if (args[5] == "false") 10 else args[5].toInt()
                                        }
                                    }
                                CustomGUI.saveData(data)
                                CustomGUI.reload()
                                when (args[4]) {
                                    in arrayOf("gui", "text") -> sender.sendMessage(
                                        "§a${args[1]}§r GUI의 " +
                                            (if (args[3] == "all") "§6모든§r" else "§6${args[3].toInt() + 1}§r번째") +
                                            " 아이템의 상호작용이 수정되었습니다."
                                    )
                                    in arrayOf("price", "sellPrice") -> sender.sendMessage(
                                        "§a${args[1]}§r GUI의 " +
                                            (if (args[3] == "all") "§6모든§r" else "§6${args[3].toInt() + 1}§r번째") +
                                            " 아이템의 ${if (args[4] == "price") "구매" else "판매"} 가격이 " +
                                            (if (args[5] != "false")
                                                "§6${DecimalFormat("#,###").format(args[5].toDouble())}§r원"
                                            else "§6(설정 안됨)§r") +
                                            "으로 설정되었습니다."
                                    )
                                    in arrayOf("minPrice", "maxPrice") -> sender.sendMessage(
                                        "§a${args[1]}§r GUI의 " +
                                            (if (args[3] == "all") "§6모든§r" else "§6${args[3].toInt() + 1}§r번째") +
                                            " 아이템의 ${if (args[4] == "minPrice") "최소" else "최대"} 가격이 §6" +
                                            (if (args[5] != "false") args[5] else if (args[4] == "minPrice") 0.6 else 1.4) +
                                            "배§r로 설정되었습니다."
                                    )
                                    "priceChange" -> sender.sendMessage(
                                        "§a${args[1]}§r GUI의 " +
                                            (if (args[3] == "all") "§6모든§r" else "§6${args[3].toInt() + 1}§r번째") +
                                            " 아이템의 가격 변경이 §6" +
                                            (if (args[5] == "true") "활성화" else "비활성화") +
                                            "§r로 설정되었습니다."
                                    )
                                    "moneyType" -> sender.sendMessage(Config.format(
                                        "§a${args[1]}§r GUI의 " +
                                            (if (args[3] == "all") "§6모든§r" else "§6${args[3].toInt() + 1}§r번째") +
                                            " 아이템의 화폐가 §6" +
                                            (if (args[5] == "cash") "$(cash)" else "$(money)") +
                                            "§r(으)로 설정되었습니다."
                                    ))
                                    "gacha" -> sender.sendMessage(
                                        "§a${args[1]}§r GUI의 " +
                                            (if (args[3] == "all") "§6모든§r" else "§6${args[3].toInt() + 1}§r번째") +
                                            " 뽑기 확률이 §6" + (if (args[5] == "false") 10 else args[5]) + "%§r로 설정되었습니다."
                                    )
                                }
                            }
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