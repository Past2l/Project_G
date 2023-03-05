package me.past2l.project_g.packet

import me.past2l.project_g.nms.NMS
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Packet {
    companion object {
        lateinit var read: (Player, Any?) -> Unit

        fun init() {
            Bukkit.getOnlinePlayers().forEach {
                NMS.injectPacket(it, read)
            }
        }

        fun remove() {
            Bukkit.getOnlinePlayers().forEach {
                NMS.removeInjectPacket(it)
            }
        }

        fun setEvent(
            read: (Player, Any?) -> Unit = { _, _ -> }
        ) {
            Companion.read = read
        }
    }
}