package me.past2l.minefarm.gui

import me.past2l.api.gui.GUI
import me.past2l.minefarm.type.gui.GUIData
import me.past2l.minefarm.type.gui.GUIGachaItem
import me.past2l.minefarm.type.gui.GUIItem
import me.past2l.minefarm.type.gui.GUIShopItem
import me.past2l.api.type.interact.Interaction
import me.past2l.minefarm.type.shop.ShopInteraction
import me.past2l.api.util.File
import me.past2l.api.util.Item
import me.past2l.api.util.Yaml
import me.past2l.minefarm.util.Config
import org.bukkit.entity.Player

class CustomGUI {
    companion object {
        val templates = mutableListOf("empty", "shop", "gacha")
        val gui = hashMapOf<String, GUI>()
        val data = hashMapOf<String, GUIData>()

        fun open(gui: String?, player: Player) {
            Companion.gui[gui]?.open(player)
        }

        fun init() {
            File.list("gui").forEach {
                val data = loadData(it) ?: return@forEach
                create(data)
            }
        }

        fun create(data: GUIData) {
            val gui = GUI(
                title = data.name,
                lines = data.line,
                closeEvent = { event, _ ->
                    me.past2l.api.entity.Player.saveData(event.player as Player)
                }
            )
            when (data.type) {
                "empty" -> emptyTemplate(gui, data)
                "shop" -> shopTemplate(gui, data)
                "gacha" -> gachaTemplate(gui, data)
                else -> return
            }
            Companion.gui[data.id] = gui
            Companion.data[data.id] = data
            saveData(data)
        }

        fun reload(data: GUIData) {
            saveData(data)
            gui.remove(data.id)
            create(data)
        }

        fun reload() {
            data.values.forEach { reload(it) }
        }

        fun remove(data: GUIData) {
            File.remove("gui/${data.id}.yml")
            gui.remove(data.id)
            Companion.data.remove(data.id)
        }

        fun loadData(path: String): GUIData? {
            val map = Yaml.read("gui/$path")
            if (
                map?.get("type")?.toString() == null ||
                map["name"]?.toString() == null ||
                map["id"]?.toString() == null
            )   return null
            val data = GUIData(
                type = map["type"].toString(),
                id = map["id"].toString(),
                name = map["name"].toString(),
                line = map["line"]?.toString()?.toInt() ?: 6,
                items = arrayListOf()
            )
            val items = map["items"] as List<*>
            items.forEach { item ->
                item as HashMap<*, *>?
                if (
                    item?.get("item")?.toString().isNullOrEmpty() ||
                    item?.get("slot")?.toString().isNullOrEmpty()
                )
                    return null
                val shop = item?.get("shop") as HashMap<*, *>?
                val interaction = item?.get("interaction") as HashMap<*, *>?
                val gacha = item?.get("gacha") as HashMap<*, *>?
                data.items.add(
                    GUIItem(
                        item = item?.get("item").toString(),
                        slot = item?.get("slot").toString().toInt(),
                        interaction = if (interaction == null) null
                        else Interaction(
                            type = interaction["type"]?.toString(),
                            id = interaction["id"]?.toString(),
                        ),
                        shop = if (shop == null) null
                        else GUIShopItem(
                            price = shop["price"]?.toString()?.toDouble(),
                            sellPrice = shop["sellPrice"]?.toString()?.toDouble(),
                            priceChange = shop["priceChange"]?.toString()?.toBoolean() ?: false,
                            previousPrice = shop["previousPrice"]?.toString()?.toDouble(),
                            previousSellPrice = shop["previousSellPrice"]?.toString()?.toDouble(),
                            minPrice = shop["minPrice"]?.toString()?.toDouble() ?: 0.6,
                            maxPrice = shop["maxPrice"]?.toString()?.toDouble() ?: 1.4,
                            moneyType = shop["moneyType"]?.toString() ?: "money",
                        ),
                        gacha = if (gacha == null) null
                        else GUIGachaItem(gacha["percent"]?.toString()?.toInt())
                    )
                )
            }
            return data
        }

        fun saveData(data: GUIData, path: String? = null) {
            val map = hashMapOf(
                "type" to data.type,
                "id" to data.id,
                "name" to data.name,
                "line" to data.line,
                "items" to arrayListOf<HashMap<String, *>>()
            )
            val list = map["items"] as ArrayList<HashMap<String, *>>
            data.items.forEach {
                val result: HashMap<String, Any?> = hashMapOf(
                    "item" to it.item,
                    "slot" to it.slot,
                )
                if (it.shop != null)
                    result["shop"] = hashMapOf(
                        "price" to it.shop?.price,
                        "sellPrice" to it.shop?.sellPrice,
                        "priceChange" to (it.shop?.priceChange ?: false),
                        "previousPrice" to it.shop?.previousPrice,
                        "previousSellPrice" to it.shop?.previousSellPrice,
                        "minPrice" to (it.shop?.minPrice ?: 0.6),
                        "maxPrice" to (it.shop?.maxPrice ?: 1.4),
                        "moneyType" to (it.shop?.moneyType ?: "money"),
                    )
                if (it.interaction != null)
                    result["interaction"] = hashMapOf(
                        "type" to it.interaction?.type,
                        "id" to it.interaction?.id,
                    )
                if (it.gacha != null)
                    result["gacha"] = hashMapOf(
                        "percent" to it.gacha?.percent
                    )
                list.add(result)
            }
            Yaml.write(path ?: "gui/${data.id}.yml", map)
        }

        private fun emptyTemplate(gui: GUI, data: GUIData) {
            data.items.forEach {
                if (it.item.isEmpty() || data.line * 9 <= it.slot)
                    return@forEach
                val item = Item.deserialize(it.item)
                gui.setItem(it.slot, item.item) { event ->
                    when (it.interaction?.type) {
                        "text" -> event.whoClicked.sendMessage(
                            Config.format(
                                it.interaction!!.id,
                                event.whoClicked  as Player
                            )
                        )
                        "gui" -> if (it.interaction!!.id != null) open(it.interaction!!.id!!, event.whoClicked as Player)
                    }
                }
            }
        }

        private fun shopTemplate(gui: GUI, data: GUIData) {
            data.items.forEach {
                if (it.item.isEmpty() || data.line * 9 <= it.slot)
                    return@forEach
                val item = Item.deserialize(it.item)
                val lore = arrayListOf<String>()
                item.item.itemMeta?.lore?.forEach { str -> lore.add(str) }
                if ((it.shop?.price != null || it.shop?.sellPrice != null) && lore.size > 0)
                    lore.add("")
                if (it.shop?.price != null)
                    lore.add(Config.format(Config.text.shop.buyItemPrice, shopItem = it.shop))
                if (it.shop?.sellPrice != null)
                    lore.add(Config.format(Config.text.shop.sellItemPrice, shopItem = it.shop))
                if (it.shop?.price != null || it.shop?.sellPrice != null)
                    lore.add("")
                if (it.shop?.price != null) {
                    lore.add(Config.format(Config.text.shop.buyItemLore))
                    lore.add(Config.format(Config.text.shop.buyAllItemLore))
                }
                if (it.shop?.sellPrice != null) {
                    lore.add(Config.format(Config.text.shop.sellItemLore))
                    lore.add(Config.format(Config.text.shop.sellAllItemLore))
                }
                item.setLore(lore)
                gui.setItem(it.slot, item.item) { event ->
                    var mode = 0
                    if (event.click.isRightClick) mode++
                    if (event.click.isShiftClick) mode += 2
                    val playerData = me.past2l.api.entity.Player.data[event.whoClicked.uniqueId]!!
                    if (mode % 2 == 0) { // Right Click (Buy)
                        if (it.shop?.price == null) return@setItem
                        val amount = if (mode / 2 == 0) 1 else 10 // Check is Shift Click
                        if (
                            (it.shop?.moneyType == "money" && playerData.money != -1.0
                                && playerData.money < (it.shop?.price ?: 0.0) * amount) ||
                            (it.shop?.moneyType == "cash" && playerData.cash != -1.0
                                && playerData.cash < (it.shop?.price ?: 0.0) * amount)
                        )
                            event.whoClicked.sendMessage("§c아이템을 구매할 돈이 부족합니다.§r")
                        else if (!Item.canGiveItem(event.whoClicked as Player, Item.deserialize(it.item).item, amount))
                            event.whoClicked.sendMessage("§c인벤토리 공간이 부족하여 아이템을 구매할 수 없습니다.§r")
                        else {
                            if (it.shop?.moneyType == "money" && playerData.money != -1.0)
                                playerData.money -= (it.shop?.price ?: 0.0) * amount
                            if (it.shop?.moneyType == "cash" && playerData.cash != -1.0)
                                playerData.cash -= (it.shop?.price ?: 0.0) * amount
                            me.past2l.api.entity.Player.onChangeData(event.whoClicked as Player)
                            Item.giveItemAmount(event.whoClicked as Player, Item.deserialize(it.item).item, amount)
                            event.whoClicked.sendMessage(Config.format(
                                Config.text.shop.item,
                                shopItem = it.shop,
                                shopInteraction = ShopInteraction(
                                    name = item.item.itemMeta?.displayName ?: "",
                                    amount = amount,
                                    type = "구매",
                                ),
                            ))
                        }
                    } else { // Left Click (Sell)
                        if (it.shop?.sellPrice == null) return@setItem
                        val playerItemAmount = Item.getItemAmount(event.whoClicked as Player, Item.deserialize(it.item).item)
                        val amount = if (mode / 2 == 0) 1 else playerItemAmount // Check is Shift Click
                        if (playerItemAmount < 1)
                            event.whoClicked.sendMessage("§c아이템이 부족하여 판매할 수 없습니다.§r")
                        else {
                            if (it.shop?.moneyType == "money" && playerData.money != -1.0)
                                playerData.money += (it.shop?.sellPrice ?: 0.0) * amount
                            if (it.shop?.moneyType == "cash" && playerData.cash != -1.0)
                                playerData.cash += (it.shop?.sellPrice ?: 0.0) * amount
                            me.past2l.api.entity.Player.onChangeData(event.whoClicked as Player)
                            Item.removeItemAmount(event.whoClicked as Player, Item.deserialize(it.item).item, amount)
                            event.whoClicked.sendMessage(Config.format(
                                Config.text.shop.item,
                                shopItem = it.shop,
                                shopInteraction = ShopInteraction(
                                    name = item.item.itemMeta?.displayName ?: "",
                                    amount = amount,
                                    type = "판매",
                                ),
                            ))
                        }
                    }
                }
            }
        }

        private fun gachaTemplate(gui: GUI, data: GUIData) {

        }
    }
}