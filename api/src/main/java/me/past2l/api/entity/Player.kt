package me.past2l.api.entity

import me.past2l.api.gui.GUI
import me.past2l.api.gui.Scoreboard
import me.past2l.api.gui.TabList
import me.past2l.api.type.player.PlayerData
import me.past2l.api.util.Yaml
import org.bukkit.BanList
import org.bukkit.Bukkit
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.HashMap

class Player {
    companion object {
        val gui: HashMap<UUID, GUI> = HashMap()
        val data: HashMap<UUID, PlayerData> = HashMap()

        fun banPlayer(player: org.bukkit.entity.Player, expires: Date? = null) {
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.name, null, expires, null)
            player.kickPlayer("You are banned from this server")
        }

        fun onChangeData(player: org.bukkit.entity.Player) {
            TabList.setPlayerName()
            Scoreboard.set(player)
            saveData(player)
        }

        fun loadData(player: org.bukkit.entity.Player): PlayerData {
            val data = Yaml.read("player/${player.uniqueId}.yml")
            val lastPlayed = data?.get("lastPlayed")?.toString()
            val default = PlayerData()
            return PlayerData(
                prefix = data?.get("prefix")?.toString() ?: default.prefix,
                money = data?.get("money")?.toString()?.toDouble() ?: default.money,
                cash = data?.get("cash")?.toString()?.toDouble() ?: default.cash,
                like = data?.get("like")?.toString()?.toDouble() ?: default.like,
                playtime = data?.get("playtime")?.toString()?.toDouble() ?: default.playtime,
                lastPlayed = if (lastPlayed != null)
                    ZonedDateTime.parse(lastPlayed)
                else default.lastPlayed
            )
        }

        fun saveData(player: org.bukkit.entity.Player) {
            val saveData = data[player.uniqueId] ?: return
            val hashMap = hashMapOf(
                "prefix" to if (!saveData.prefix.endsWith("&r") && saveData.prefix.isNotEmpty())
                    saveData.prefix + "&r" else saveData.prefix,
                "money" to saveData.money,
                "cash" to saveData.cash,
                "like" to saveData.like,
                "playtime" to saveData.playtime,
                "lastPlayed" to saveData.lastPlayed.toString(),
            )
            Yaml.write("player/${player.uniqueId}.yml", hashMap)
            data[player.uniqueId] = loadData(player)
        }

        fun loadData() {
            for(player in Bukkit.getServer().onlinePlayers)
                data[player.uniqueId] = loadData(player)
        }

        fun saveData() {
            for(player in Bukkit.getServer().onlinePlayers) saveData(player)
        }
    }
}