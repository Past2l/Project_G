package me.past2l.minefarm.type.gui

data class GUIShopItem(
    var price: Double? = null,
    var sellPrice: Double? = null,
    var priceChange: Boolean = false,
    var previousPrice: Double? = null,
    var previousSellPrice: Double? = null,
    var minPrice: Double = 0.6,
    var maxPrice: Double = 1.4,
    var moneyType: String = "money", // money or cash
)
