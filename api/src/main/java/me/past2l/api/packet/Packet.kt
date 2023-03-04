package me.past2l.api.packet

import me.past2l.api.nms.NMS
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
            this.read = read
        }
    }
}