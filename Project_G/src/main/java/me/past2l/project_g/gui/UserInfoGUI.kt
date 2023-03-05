package me.past2l.project_g.gui

import me.past2l.project_g.util.Item
import me.past2l.project_g.config.Config
import org.bukkit.Material
import org.bukkit.entity.Player
import java.text.DecimalFormat
import java.time.ZoneId
import java.time.ZonedDateTime

class UserInfoGUI {
    companion object {
        fun functionList(player: Player, target: Player) {
            val gui = GUI(target.displayName, 3)
            val data = me.past2l.project_g.entity.Player.data
            gui.setItem(
                11,
                Item.playerHead(target)
                    .setName("§6${target.name}§f의 정보")
                    .setLore(
                        listOf(
                            "&r&f이름 : &6${target.name}&r",
                            "&r&f칭호 : &6%player.prefix.exist%&r",
                            "&r&f호감도 : &d♥ %player.like%&r"
                        ).map {
                            Config.format(it, player = target)
                        } as MutableList
                    )
                    .item
            )
            gui.setItem(
                15,
                Item(Material.NETHER_STAR, 1)
                    .setName("§d§l호감도§r")
                    .setLore(arrayListOf(
                        "",
                        "§a좌클릭§f을 하여 상대의 §a호감도를 올릴 수§f 있습니다.",
                        "§a우클릭§f을 하여 상대의 §c호감도를 내릴 수§f 있습니다."
                    ))
                    .item
            ) {
                if (ZonedDateTime.now(ZoneId.of(Config.timezone)) > data[player.uniqueId]!!.likeEnable) {
                    val value = if (it.isLeftClick) 1 else -1
                    data[target.uniqueId]!!.like += value
                    data[player.uniqueId]!!.likeEnable = ZonedDateTime.now(ZoneId.of(Config.timezone)).plusHours(1)
                    me.past2l.project_g.entity.Player.onChangeData(target)
                    me.past2l.project_g.entity.Player.onChangeData(player)
                    target.sendMessage("§${if (value == 1) "a" else "c"}" +
                        "${player.name}§r님께서 호감도를 ${if (value == 1) "올" else "내"}려주었습니다. " +
                    "§7(현재 호감도 : ${DecimalFormat("#,###").format(data[target.uniqueId]!!.like)})")
                    player.sendMessage("§${if (value == 1) "a" else "c"}" +
                        "${target.name}§r님의 호감도를 ${if (value == 1) "올" else "내"}렸습니다. " +
                    "§7(${target.name}의 호감도 : ${DecimalFormat("#,###").format(data[target.uniqueId]!!.like)})")
                } else {
                    val time = data[player.uniqueId]!!.likeEnable.toEpochSecond() -
                        ZonedDateTime.now(ZoneId.of(Config.timezone)).toEpochSecond()
                    val h = time / 3600
                    val m = (time % 3600) / 60
                    val s = time % 60
                    val result = ((if (h > 0) "${h}시간 " else "") +
                        (if (m > 0) "${m}분 " else "") +
                        (if (s > 0) "${s}초" else "")).trim()
                    player.sendMessage("§c$result 후에 호감도를 올리거나 내릴 수 있습니다.§r")
                }
            }
            gui.open(player)
        }

        fun open(player: Player, target: Player) {
            functionList(player, target)
        }
    }
}