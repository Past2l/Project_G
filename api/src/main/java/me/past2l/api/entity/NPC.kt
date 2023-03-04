package me.past2l.api.entity

import me.past2l.api.nms.NMS
import me.past2l.api.type.entity.FakePlayer
import me.past2l.api.type.entity.NPCData
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class NPC(
    val data: NPCData,
    var clickEvent: ((Player) -> Unit)? = null
) {
    private val fakePlayer = FakePlayer(data.id, data.name, data.uuid, data.location, data.skin)
    val id: Int

    companion object {
        val npcs = hashMapOf<String, NPC>()

        fun spawn() { npcs.values.forEach { it.spawn() } }
        fun spawn(player: Player) { npcs.values.forEach { it.spawn(player) } }
        fun render() { npcs.values.forEach { it.render() } }
        fun render(player: Player) { npcs.values.forEach { it.render(player) } }
        fun remove() { npcs.values.forEach { it.remove() } }
        fun onInteractNPC(packet: Any?, func: (Int) -> Unit) {
            NMS.onInteractEntity(packet) { id -> func(id) }
        }
    }

    init {
        if (npcs[data.id] == null) {
            id = NMS.createFakePlayer(fakePlayer)
            npcs[data.id] = this
        } else
            id = -1
    }

    fun spawn(player: Player) {
        if (id == -1) return
        NMS.spawnFakePlayer(player, fakePlayer)
    }

    fun spawn() {
        if (id == -1) return
        Bukkit.getOnlinePlayers().forEach { spawn(it) }
    }

    fun render(player: Player, delay: Long = 1) {
        if (id == -1) return
        NMS.renderFakePlayer(player, fakePlayer, delay)
    }

    fun render(delay: Long = 1) {
        if (id == -1) return
        Bukkit.getOnlinePlayers().forEach { render(it, delay) }
    }

    fun remove(player: Player) {
        if (id == -1) return
        NMS.removeFakePlayer(player, fakePlayer)
    }

    fun remove() {
        if (id == -1) return
        Bukkit.getOnlinePlayers().forEach { remove(it) }
        NMS.removeFakePlayerData(fakePlayer)
        npcs.remove(data.id)
    }
}