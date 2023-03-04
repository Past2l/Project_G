package me.past2l.api.nms

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import me.past2l.api.type.nms.NMS
import me.past2l.api.type.entity.FakePlayer
import net.minecraft.server.v1_12_R1.*
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_12_R1.CraftServer
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.HashMap

class v1_12_R1(val plugin: JavaPlugin): NMS {
    private val npcs: HashMap<String, EntityPlayer> = hashMapOf()

    private fun stringToComponent(str: String): String {
        return "[${str.split("\n").joinToString(",") {
            "{\"text\":\"${it}\"}"
        }}]"
    }

    override fun setTabList(player: Player, header: String, footer: String) {
        val connection = (player as CraftPlayer).handle.playerConnection
        val packet = PacketPlayOutPlayerListHeaderFooter()
        setValue(packet, "a", ChatSerializer.a(stringToComponent(header))!!)
        setValue(packet, "b", ChatSerializer.a(stringToComponent(footer))!!)
        connection.sendPacket(packet)
    }

    override fun createFakePlayer(data: FakePlayer): Int {
        val craftServer = (this.plugin.server as CraftServer).server
        val craftWorld = (data.location.world as CraftWorld).handle
        val profile = GameProfile(data.uuid, data.name.replace("-", " "))
        profile.properties.put("textures", Property("textures", data.skin.texture, data.skin.signature))
        npcs[data.id] = EntityPlayer(craftServer, craftWorld, profile, PlayerInteractManager(craftWorld))
        return npcs[data.id]!!.id
    }

    override fun spawnFakePlayer(player: Player, data: FakePlayer) {
        val npc = npcs[data.id] ?: return
        npc.setLocation(data.location.x, data.location.y, data.location.z, data.location.yaw, data.location.pitch)
        val connection = (player as CraftPlayer).handle.playerConnection
        val yaw = (data.location.yaw * 256 / 360).toInt().toByte()
        val pitch = (data.location.pitch * 256 / 360).toInt().toByte()
        connection.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc))
        connection.sendPacket(PacketPlayOutNamedEntitySpawn(npc))
        connection.sendPacket(PacketPlayOutEntityHeadRotation(npc, yaw))
        connection.sendPacket(PacketPlayOutEntity.PacketPlayOutEntityLook(npc.id, yaw, pitch, true))
        renderFakePlayer(player, data, 40)
    }

    override fun renderFakePlayer(player: Player, data: FakePlayer, delay: Long) {
        val npc = npcs[data.id] ?: return
        val connection = (player as CraftPlayer).handle.playerConnection
        val watcher = npc.dataWatcher
        watcher.set(DataWatcherRegistry.a.a(13), 127.toByte())
        connection.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc))
        connection.sendPacket(PacketPlayOutEntityMetadata(npc.id, watcher, true))
        Bukkit.getScheduler().runTaskLater(
            plugin,
            {
                connection.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc))
            },
            delay
        )
    }

    override fun removeFakePlayer(player: Player, data: FakePlayer) {
        val npc = npcs[data.id] ?: return
        val connection = (player as CraftPlayer).handle.playerConnection
        connection.sendPacket(PacketPlayOutEntityDestroy(npc.id))
        npcs.remove(data.id)
    }

    override fun removeFakePlayerData(data: FakePlayer) {
        npcs.remove(data.id)
    }

    override fun onInteractEntity(packet: Any?, event: (id: Int) -> Unit) {
        if (packet is PacketPlayInUseEntity) {
            val id = getValue(packet, "a") as Int
            val action = getValue(packet, "action").toString()
            val hand = getValue(packet, "d").toString()
            if (action == "INTERACT" && hand == "MAIN_HAND")
                event(id)
        }
    }

    override fun injectPacket(player: Player, read: (Player, Any?) -> Unit) {
        val pipeline = (player as CraftPlayer).handle.playerConnection.networkManager.channel.pipeline()
        pipeline.addBefore(
            "packet_handler",
            player.name,
            object : ChannelDuplexHandler() {
                @Throws(Exception::class)
                override fun channelRead(channelHandlerContext: ChannelHandlerContext?, packet: Any?) {
                    Bukkit.getScheduler().runTask(
                        plugin
                    ) { read(player, packet) }
                    super.channelRead(channelHandlerContext, packet)
                }
            }
        )
    }

    override fun removeInjectPacket(player: Player) {
        val channel = (player as CraftPlayer).handle.playerConnection.networkManager.channel
        channel.eventLoop().submit { channel.pipeline().remove(player.name) }
    }

    private fun getValue(instance: Any, name: String): Any? {
        var result: Any? = null
        try {
            val field = instance.javaClass.getDeclaredField(name)
            field.isAccessible = true
            result = field.get(instance)
            field.isAccessible = false
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    private fun setValue(obj: Any, key: String, value: Any) {
        val field = obj.javaClass.getDeclaredField(key)
        field.isAccessible = true
        field.set(obj, value)
    }
}