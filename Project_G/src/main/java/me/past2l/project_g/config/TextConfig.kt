package me.past2l.project_g.config

import me.past2l.project_g.type.config.text.ConfigText
import me.past2l.project_g.type.config.text.ConfigTextShop
import me.past2l.project_g.type.gui.GUIShopItem
import me.past2l.project_g.type.shop.ShopInteraction
import me.past2l.project_g.util.Yaml
import java.text.DecimalFormat

class TextConfig {
    companion object {
        lateinit var config: ConfigText
        private var forceReplace = false

        lateinit var shop: ConfigTextShop

        fun init() {
            val data = Yaml.read("text.yml")
            val shopText = data?.get("shop") as HashMap<*, *>?
            val default = ConfigText()

            forceReplace = data?.get("forceReplace")?.toString()?.toBoolean() ?: false

            config = if (forceReplace) default
            else ConfigText(
                shop = ConfigTextShop(
                    item = shopText?.get("item")?.toString() ?: default.shop.item,
                    buyItemPrice = shopText?.get("buyItemPrice")?.toString() ?: default.shop.buyItemPrice,
                    buyItemLore = shopText?.get("buyItemLore")?.toString() ?: default.shop.buyItemLore,
                    buyAllItemLore = shopText?.get("buyAllItemLore")?.toString()
                        ?: default.shop.buyAllItemLore,
                    sellItemPrice = shopText?.get("sellItemPrice")?.toString() ?: default.shop.sellItemPrice,
                    sellItemLore = shopText?.get("sellItemLore")?.toString() ?: default.shop.sellItemLore,
                    sellAllItemLore = shopText?.get("sellAllItemLore")?.toString()
                        ?: default.shop.sellAllItemLore,
                )
            )

            this.shop = config.shop
        }

        fun save() {
            val data = hashMapOf<String, Any>(
                "shop" to hashMapOf(
                    "item" to Config.config.text.shop.item,
                    "buyItemPrice" to Config.config.text.shop.buyItemPrice,
                    "buyItemLore" to Config.config.text.shop.buyItemLore,
                    "buyAllItemLore" to Config.config.text.shop.buyAllItemLore,
                    "sellItemPrice" to Config.config.text.shop.sellItemPrice,
                    "sellItemLore" to Config.config.text.shop.sellItemLore,
                    "sellAllItemLore" to Config.config.text.shop.sellAllItemLore,
                ),
            )
            if (forceReplace) data["forceReplace"] = true
            Yaml.write("text.yml", data)
        }

        fun format(
            str: String?,
            shopItem: GUIShopItem? = null,
            shopInteraction: ShopInteraction? = null,
            trim: Boolean = true,
        ): String {
            var result = str ?: return ""
            if (shopItem != null) {
                result = result.replace("%shop.item.moneyType%", shopItem.moneyType)
                    .replace(
                        "%shop.item.buyPrice%",
                        DecimalFormat("#,###").format(shopItem.price ?: 0.0)
                    )
                    .replace(
                        "%shop.item.previousBuyPrice%",
                        DecimalFormat("#,###").format(shopItem.previousPrice ?: 0.0)
                    )
                    .replace(
                        "%shop.item.sellPrice%",
                        DecimalFormat("#,###").format(shopItem.sellPrice ?: 0.0)
                    )
                    .replace(
                        "%shop.item.previousSellPrice%",
                        DecimalFormat("#,###").format(shopItem.previousSellPrice ?: 0.0)
                    )
                    .replace(
                        "%shop.item.gap.buyPrice%",
                        if (!shopItem.priceChange) ""
                        else {
                            val gap = (shopItem.price ?: 0.0) - (shopItem.previousPrice ?: shopItem.price ?: 0.0)
                            if (gap < 1 && gap > -1) "(-)"
                            else if (gap > 0)
                                "&r&f(&c▲${DecimalFormat("#,###").format(gap)}&f)"
                            else
                                "&r&f(&9▼${DecimalFormat("#,###").format(-gap)}&f)"
                        }
                    )
                    .replace(
                        "%shop.item.gap.sellPrice%",
                        if (!shopItem.priceChange) ""
                        else {
                            val gap = (shopItem.sellPrice ?: 0.0) - (shopItem.previousSellPrice ?: shopItem.sellPrice ?: 0.0)
                            if (gap < 1 && gap > -1) "(-)"
                            else if (gap > 0)
                                "(&c▲${DecimalFormat("#,###").format(gap)}&f)"
                            else
                                "(&9▼${DecimalFormat("#,###").format(-gap)}&f)"
                        }
                    )
                if (shopInteraction != null) {
                    result = result.replace(
                        "%shop.item.name%",
                        if (shopInteraction.name.isNotEmpty()) shopInteraction.name + "&r"
                        else ""
                    )
                        .replace("%shop.item.type%", shopInteraction.type)
                        .replace("%shop.item.amount%", shopInteraction.amount.toString())
                        .replace(
                            "%shop.item.price%",
                            DecimalFormat("#,###").format(
                                when (shopInteraction.type) {
                                    "구매" -> (shopItem.price ?: 0.0) * shopInteraction.amount
                                    "판매" -> (shopItem.sellPrice ?: 0.0) * shopInteraction.amount
                                    else -> ""
                                }
                            )
                        )
                }
            }
            return Config.format(result, trim = trim)
        }
    }
}