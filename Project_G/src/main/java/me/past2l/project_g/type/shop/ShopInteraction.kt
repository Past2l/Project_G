package me.past2l.minefarm.type.shop

data class ShopInteraction(
    val name: String,
    val type: String, // 구매 or 판매
    val amount: Int,
)