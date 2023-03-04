package me.past2l.project_g.type.gui

import me.past2l.api.type.interact.Interaction

data class GUIItem (
    var item: String,
    var slot: Int,
    var interaction: Interaction? = null,
    var shop: GUIShopItem? = null,
    var gacha: GUIGachaItem? = null,
)