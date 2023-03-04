package me.past2l.api.nms

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import me.past2l.api.type.nms.NMS
import me.past2l.api.type.entity.FakePlayer
import net.minecraft.network.chat.IChatBaseComponent.ChatSerializer
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.DataWatcherRegistry
import net.minecraft.server.level.EntityPlayer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R1.CraftServer
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashMap

class v1_19_R1(val plugin: JavaPlugin): NMS {
    private val npcs: HashMap<String, EntityPlayer> = hashMapOf()
    private val channels: HashMap<UUID, Channel> = hashMapOf()
    private val interactNPC: HashMap<Int, Boolean> = hashMapOf()

    private fun stringToComponent(str: String): String {
        return "[${str.split("\n").joinToString(",") {
            "{\"text\":\"${it}\"}"
        }}]"
    }

    override fun setTabList(player: Player, header: String, footer: String) {
        val packet = PacketPlayOutPlayerListHeaderFooter(
            ChatSerializer.a(stringToComponent(header)),
            ChatSerializer.a(stringToComponent(footer))
        )
        val connection = (player as CraftPlayer).handle.b
        connection.a(packet)
    }

    override fun createFakePlayer(data: FakePlayer): Int {
        val craftServer = (this.plugin.server as CraftServer).server
        val craftWorld = (data.location.world as CraftWorld).handle
        val profile = GameProfile(data.uuid, data.name.replace("-", " "))
        profile.properties.put("textures", Property("textures", data.skin.texture, data.skin.signature))
        npcs[data.id] = EntityPlayer(craftServer, craftWorld, profile, null)
        return npcs[data.id]!!.ae()
    }

    override fun spawnFakePlayer(player: Player, data: FakePlayer) {
        val npc = npcs[data.id] ?: return
        npc.a(data.location.x, data.location.y, data.location.z, data.location.yaw, data.location.pitch)
        val connection = (player as CraftPlayer).handle.b
        val yaw = (data.location.yaw * 256 / 360).toInt().toByte()
        val pitch = (data.location.pitch * 256 / 360).toInt().toByte()
        connection.a(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc))
        connection.a(PacketPlayOutNamedEntitySpawn(npc))
        connection.a(PacketPlayOutEntityHeadRotation(npc, yaw))
        connection.a(PacketPlayOutEntity.PacketPlayOutEntityLook(npc.ae(), yaw, pitch, true))
        renderFakePlayer(player, data, 40)
    }

    override fun renderFakePlayer(player: Player, data: FakePlayer, delay: Long) {
        val npc = npcs[data.id] ?: return
        val connection = (player as CraftPlayer).handle.b
        val watcher = npc.ai()
        watcher.registrationLocked = false
        watcher.b(DataWatcherRegistry.a.a(17), 127.toByte())
        connection.a(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc))
        connection.a(PacketPlayOutEntityMetadata(npc.ae(), watcher, true))
        Bukkit.getScheduler().runTaskLater(
            plugin,
            Runnable {
                connection.a(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, npc))
            },
            delay
        )
    }

    override fun removeFakePlayer(player: Player, data: FakePlayer) {
        val npc = npcs[data.id] ?: return
        val connection = (player as CraftPlayer).handle.b
        connection.a(PacketPlayOutEntityDestroy(npc.ae()))

    }

    override fun removeFakePlayerData(data: FakePlayer) {
        npcs.remove(data.id)
    }

    override fun onInteractEntity(packet: Any?, event: (id: Int) -> Unit) {
        if (packet is PacketPlayInUseEntity) {
            val id = getValue(packet, "a") as Int
            val hand = getValue(packet, "b").toString().split("@")[0]
            if (hand == "net.minecraft.network.protocol.game.PacketPlayInUseEntity\$e")
                if (interactNPC[id] != true) interactNPC[id] = true
                else {
                    interactNPC.remove(id)
                    event(id)
                }
        }
    }

    override fun injectPacket(player: Player, read: (Player, Any?) -> Unit) {
        val channel = (player as CraftPlayer).handle.b.a().m
        channels[player.uniqueId] = channel
        if (channel.pipeline().get("PacketInjector") != null) return
        channel.pipeline().addAfter(
            "decoder",
            "PacketInjector",
            object : MessageToMessageDecoder<Packet<*>>() {
                override fun decode(
                    ctx: ChannelHandlerContext?,
                    packet: Packet<*>?,
                    out: MutableList<Any>?
                ) {
                    Bukkit.getScheduler().runTask(plugin, Runnable { read(player, packet) })
                    if (packet != null) out?.add(packet)
                }
            }
        )
    }

    override fun removeInjectPacket(player: Player) {
        val channel = channels[player.uniqueId]
        if (channel?.pipeline()?.get("PacketInjector") != null)
            channel.pipeline().remove("PacketInjector")
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