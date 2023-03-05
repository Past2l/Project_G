package me.past2l.api.type.config

data class ConfigData(
    val serverName: String = "Test Server",
    val timezone: String = "Asia/Seoul",
    val resourcePack: String = "",
    val consolePrefix: String = "&6[&a%server.name%&6]&r",
    val chat: String = "%player.prefix% %player.name% > %chat.message%",
    val enable: ConfigEnable = ConfigEnable(),
    val tabList: ConfigTabList = ConfigTabList(),
    val scoreboard: ConfigScoreboard = ConfigScoreboard(),
    val motd: ConfigMOTD = ConfigMOTD(),
    val money: ConfigMoney = ConfigMoney(),
)
