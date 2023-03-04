package me.past2l.minefarm.entity

import me.past2l.api.entity.NPC
import me.past2l.minefarm.gui.CustomGUI
import me.past2l.api.type.entity.NPCData
import me.past2l.api.type.interact.Interaction
import me.past2l.api.type.skin.SkinResult
import me.past2l.minefarm.util.Config
import me.past2l.api.util.File
import me.past2l.api.util.Yaml
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.HashMap

class CustomNPC {
    companion object {
        val npcs = hashMapOf<String, NPC>()
        val config = hashMapOf<String, NPCData>()
        fun spawn(data: NPCData): NPC {
            val npc = NPC(data) {
                when (data.interaction?.type) {
                    "gui" -> CustomGUI.open(data.interaction!!.id, it)
                    "text" -> it.sendMessage(Config.format(
                        data.interaction!!.id,
                        player = it,
                        npc = data,
                    ))
                }
            }
            npc.spawn()
            npcs[data.id] = npc
            config[data.id] = data
            return npc
        }

        fun init() {
            File.list("npc").forEach {
                if (!it.endsWith(".yml")) return@forEach
                val data = loadData(it)
                spawn(data)
                saveData(npc = data)
            }
        }

        fun reload(data: NPCData) {
            npcs[data.id]?.remove()
            npcs.remove(data.id)
            spawn(data)
            saveData(npc = data)
        }

        fun remove(removeFile: Boolean = false) {
            config.values.forEach {
                if (removeFile) File.remove("npc/${it.id}.yml")
                npcs[it.id]?.remove()
            }
        }

        fun remove(id: String) {
            if (config[id] == null) return
            File.remove("npc/$id.yml")
            npcs[id]?.remove()
            npcs.remove(id)
            config.remove(id)
        }

        fun onInteractNPC(player: Player, packet: Any?) {
            NPC.onInteractNPC(packet) { id ->
                npcs.values.find { it.id == id }?.clickEvent?.let { it(player) }
            }
        }

        fun loadData(path: String): NPCData {
            val data = Yaml.read("npc/$path")
            val location = data?.get("location") as HashMap<*, *>?
            val skin = data?.get("skin") as HashMap<*, *>?
            val interaction = data?.get("interaction") as HashMap<*, *>?
            val default = NPCData(id = "test", name = "test", location = Location(
                Bukkit.getWorld("world"), 0.0, 0.0, 0.0
            ))

            var name = Config.format(data?.get("name")?.toString())
            name = if (name.length > 16) name.substring(0, 16) else name

            return NPCData(
                id = data?.get("id")?.toString() ?: default.id,
                name = name,
                location = Location(
                    Bukkit.getWorld(location?.get("world")?.toString() ?: default.location.world.name),
                    location?.get("x")?.toString()?.toDouble() ?: default.location.x,
                    location?.get("y")?.toString()?.toDouble() ?: default.location.y,
                    location?.get("z")?.toString()?.toDouble() ?: default.location.z,
                    location?.get("yaw")?.toString()?.toFloat() ?: default.location.yaw,
                    location?.get("pitch")?.toString()?.toFloat() ?: default.location.pitch,
                ),
                skin = SkinResult(
                    skin?.get("texture")?.toString() ?: default.skin.texture,
                    skin?.get("signature")?.toString() ?: default.skin.signature,
                    skin?.get("skin")?.toString() ?: default.skin.skin,
                    skin?.get("cape")?.toString() ?: default.skin.cape,
                ),
                interaction = if (interaction != null) Interaction(
                    interaction["type"]?.toString(),
                    interaction["id"]?.toString()
                ) else null
            )
        }

        fun saveData(path: String? = null, npc: NPCData) {
            val hashMap = hashMapOf(
                "id" to npc.id,
                "name" to npc.name,
                "uuid" to npc.uuid,
                "location" to hashMapOf(
                    "world" to npc.location.world.name,
                    "x" to npc.location.x,
                    "y" to npc.location.y,
                    "z" to npc.location.z,
                    "yaw" to npc.location.yaw,
                    "pitch" to npc.location.pitch,
                ),
                "skin" to hashMapOf(
                    "texture" to npc.skin.texture,
                    "signature" to npc.skin.signature,
                    "skin" to npc.skin.skin,
                    "cape" to npc.skin.cape,
                ),
            )
            if (npc.interaction != null)
                hashMap["interaction"] = hashMapOf(
                    "type" to npc.interaction!!.type,
                    "id" to npc.interaction!!.id,
                )
            Yaml.write(path ?: "npc/${npc.id}.yml", hashMap)
        }
    }
}