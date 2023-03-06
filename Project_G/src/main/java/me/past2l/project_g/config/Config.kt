package me.past2l.project_g.config

import me.past2l.project_g.type.config.ConfigEnable
import me.past2l.project_g.type.config.ConfigMOTD
import me.past2l.project_g.type.config.ConfigScoreboard
import me.past2l.project_g.type.config.ConfigTabList
import me.past2l.project_g.type.entity.NPCData
import me.past2l.project_g.type.config.ConfigMoney
import me.past2l.project_g.util.Yaml
import me.past2l.project_g.type.config.ConfigData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.text.DecimalFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Config {
    companion object {
        lateinit var config: ConfigData
        private var forceReplace = false

        lateinit var serverName: String
        lateinit var timezone: String
        lateinit var resourcePack: String
        lateinit var consolePrefix: String
        lateinit var chat: String
        lateinit var enable: ConfigEnable
        lateinit var tabList: ConfigTabList
        lateinit var scoreboard: ConfigScoreboard
        lateinit var motd: ConfigMOTD

        fun init() {
            val data = Yaml.read("config.yml")
            val enable = data?.get("enable") as HashMap<*, *>?
            val tabList = data?.get("tabList") as HashMap<*, *>?
            val scoreboard = data?.get("scoreboard") as HashMap<*, *>?
            val motd = data?.get("motd") as HashMap<*, *>?
            val money = data?.get("money") as HashMap<*, *>?
            val default = ConfigData()

            forceReplace = data?.get("forceReplace")?.toString()?.toBoolean() ?: false

            config = if (forceReplace) default
            else ConfigData(
                serverName = data?.get("serverName")?.toString() ?: default.serverName,
                timezone = data?.get("timezone")?.toString() ?: default.timezone,
                resourcePack = data?.get("resourcePack")?.toString() ?: default.resourcePack,
                consolePrefix = data?.get("consolePrefix")?.toString() ?: default.consolePrefix,
                chat = data?.get("chat")?.toString() ?: default.chat,
                enable = ConfigEnable(
                    chat = enable?.get("chat")?.toString()?.toBoolean() ?: default.enable.chat,
                    tabList = enable?.get("tabList")?.toString()?.toBoolean() ?: default.enable.tabList,
                    scoreboard = enable?.get("scoreboard")?.toString()?.toBoolean() ?: default.enable.scoreboard,
                ),
                tabList = ConfigTabList(
                    header = tabList?.get("header")?.toString() ?: default.tabList.header,
                    footer = tabList?.get("footer")?.toString() ?: default.tabList.footer,
                    playerName = tabList?.get("playerName")?.toString() ?: default.tabList.playerName,
                ),
                scoreboard = ConfigScoreboard(
                    title = scoreboard?.get("title")?.toString() ?: default.scoreboard.title,
                    content = scoreboard?.get("content") as ArrayList<String>? ?: default.scoreboard.content,
                ),
                motd = ConfigMOTD(
                    content = motd?.get("content") as ArrayList<String>? ?: default.motd.content,
                    center = motd?.get("center")?.toString()?.toBoolean() ?: default.motd.center,
                ),
                money = ConfigMoney(
                    money = money?.get("money")?.toString() ?: default.money.money,
                    cash = money?.get("cash")?.toString() ?: default.money.cash,
                ),
            )

            this.serverName = config.serverName
            this.timezone = config.timezone
            this.resourcePack = config.resourcePack
            this.consolePrefix = config.consolePrefix
            this.chat = config.chat
            this.enable = config.enable
            this.tabList = config.tabList
            this.scoreboard = config.scoreboard
            this.motd = config.motd
        }

        fun save() {
            val data = hashMapOf(
                "serverName" to config.serverName,
                "timezone" to config.timezone,
                "resourcePack" to config.resourcePack,
                "consolePrefix" to config.consolePrefix,
                "chat" to config.chat,
                "enable" to hashMapOf(
                    "chat" to config.enable.chat,
                    "tabList" to config.enable.tabList,
                    "scoreboard" to config.enable.scoreboard,
                ),
                "tabList" to hashMapOf(
                    "header" to config.tabList.header,
                    "footer" to config.tabList.footer,
                    "playerName" to config.tabList.playerName,
                ),
                "scoreboard" to hashMapOf(
                    "title" to config.scoreboard.title,
                    "content" to config.scoreboard.content,
                ),
                "motd" to hashMapOf(
                    "content" to config.motd.content,
                    "center" to config.motd.center,
                ),
                "money" to hashMapOf(
                    "money" to config.money.money,
                    "cash" to config.money.cash,
                ),
            )
            if (forceReplace) data["forceReplace"] = true
            Yaml.write("config.yml", data)
        }

        fun format(
            str: String?,
            player: Player? = null,
            npc: NPCData? = null,
            trim: Boolean = true,
        ): String {
            var result = str ?: return ""
            val temp = UUID.randomUUID().toString()
            val now = ZonedDateTime.now(ZoneId.of(config.timezone))
            if (player != null) {
                val playerData = me.past2l.project_g.entity.Player.data[player.uniqueId]!!
                result = result.replace("%player.name%", player.name)
                    .replace("%player.op%", player.isOp.toString())
                    .replace("%player.uuid%", player.uniqueId.toString())
                    .replace("%player.prefix%", playerData.prefix)
                    .replace("%player.prefix.exist%", playerData.prefix.ifEmpty { "없음" })
                    .replace("%player.playtime%", DecimalFormat("#.#").format(playerData.playtime))
                    .replace("%player.money%",
                        if (playerData.money == -1.0) "Infinity"
                        else DecimalFormat("#,###").format(playerData.money)
                    )
                    .replace("%player.cash%",
                        if (playerData.cash == -1.0) "Infinity"
                        else DecimalFormat("#,###").format(playerData.cash)
                    )
                    .replace("%player.like%", DecimalFormat("#,###").format(playerData.like))
            }
            if (npc != null) {
                result = result.replace("%npc.id%", npc.id)
                    .replace("%npc.uuid%", npc.uuid.toString())
                    .replace("%npc.name%", npc.name)
            }
            result = result
                .replace("\\&", temp)
                .replace("&", "§")
                .replace(temp, "&")
                .replace("%server.name%", config.serverName)
                .replace("%server.players%", Bukkit.getOnlinePlayers().size.toString())
                .replace("%date.year%", now.format(DateTimeFormatter.ofPattern("yyyy")))
                .replace("%date.month%", now.format(DateTimeFormatter.ofPattern("MM")))
                .replace("%date.day%", now.format(DateTimeFormatter.ofPattern("dd")))
                .replace("%date.hour%", now.format(DateTimeFormatter.ofPattern("HH")))
                .replace("%date.minute%", now.format(DateTimeFormatter.ofPattern("mm")))
                .replace("%server.money%", config.money.money)
                .replace("%server.cash%", config.money.cash)
            return if (trim) result.trim() else result
        }
    }
}