package me.past2l.api.nms

import me.past2l.api.PluginManager
import me.past2l.api.type.nms.NMS
import me.past2l.api.type.entity.FakePlayer
import me.past2l.api.util.Logger
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class NMS {
    companion object {
        private val plugin = PluginManager.plugin
        private var nms: NMS? = null

        fun getVersion(): String = Bukkit.getServer().javaClass.`package`.name.split(".")[3]

        fun init(): Boolean {
            val version = getVersion()
            nms = when (version) {
                "v1_12_R1" -> v1_12_R1(plugin)
                "v1_19_R1" -> v1_19_R1(plugin)
                else -> null
            }
            return if (nms != null) {
                Logger.log("§aNMS§r loading completed.")
                Logger.log("§aNMS§r Verson : §e${getVersion()}§r")
                true
            } else {
                Logger.error("This plugin does not currently support Minecraft versions.")
                Bukkit.getPluginManager().disablePlugin(plugin)
                false
            }
        }

        fun setTabList(player: Player, header: String, footer: String) {
            nms?.setTabList(player, header, footer)
        }

        fun createFakePlayer(data: FakePlayer): Int {
            return nms?.createFakePlayer(data) ?: -1
        }

        fun spawnFakePlayer(player: Player, data: FakePlayer) {
            nms?.spawnFakePlayer(player, data)
        }

        fun renderFakePlayer(player: Player, data: FakePlayer, delay: Long) {
            nms?.renderFakePlayer(player, data, delay)
        }

        fun removeFakePlayer(player: Player, data: FakePlayer) {
            nms?.removeFakePlayer(player, data)
        }

        fun removeFakePlayerData(data: FakePlayer) {
            nms?.removeFakePlayerData(data)
        }

        fun onInteractEntity(packet: Any?, event: (id: Int) -> Unit) {
            nms?.onInteractEntity(packet, event)
        }

        fun injectPacket(player: Player, read: (Player, Any?) -> Unit) {
            nms?.injectPacket(player, read)
        }

        fun removeInjectPacket(player: Player) {
            nms?.removeInjectPacket(player)
        }
    }
}