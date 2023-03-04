package me.past2l.api.type.nms

import me.past2l.api.type.entity.FakePlayer
import org.bukkit.entity.Player

interface NMS {
    fun setTabList(player: Player, header: String, footer: String)
    fun createFakePlayer(data: FakePlayer): Int
    fun spawnFakePlayer(player: Player, data: FakePlayer)
    fun renderFakePlayer(player: Player, data: FakePlayer, delay: Long)
    fun removeFakePlayer(player: Player, data: FakePlayer)
    fun removeFakePlayerData(data: FakePlayer)
    fun onInteractEntity(packet: Any?, event: (id: Int) -> Unit)
    fun injectPacket(player: Player, read: (Player, Any?) -> Unit = { _, _ -> })
    fun removeInjectPacket(player: Player)
}