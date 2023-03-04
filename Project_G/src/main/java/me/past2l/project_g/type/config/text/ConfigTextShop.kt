package me.past2l.minefarm.type.config.text

data class ConfigTextShop(
    val item: String = "%shop.item.name% 아이템 &6%shop.item.amount%&r개를 " +
        "&6%shop.item.price%&r%server.%shop.item.moneyType%%에 %shop.item.type%하였습니다.",
    val buyItemPrice: String = "&r&f구매 가격 : &6%shop.item.buyPrice%&r&f%server.%shop.item.moneyType%% %shop.item.gap.buyPrice%",
    val buyItemLore: String = "&r&f아이템 &61&f개 구매 : &a마우스 좌클릭&f",
    val buyAllItemLore: String = "&r&f아이템 &610&f개 구매 : &aShift&f + &a마우스 좌클릭&f",
    val sellItemPrice: String = "&r&f판매 가격 : &6%shop.item.sellPrice%&r&f%server.%shop.item.moneyType%% %shop.item.gap.sellPrice%",
    val sellItemLore: String = "&r&f아이템 &61&f개 판매 : &a마우스 우클릭&f",
    val sellAllItemLore: String = "&r&f아이템 &6모두&f 판매 : &aShift&f + &a마우스 우클릭&f",
)