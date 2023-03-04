package me.past2l.api.type.entity

import me.past2l.api.type.skin.SkinResult
import me.past2l.api.type.interact.Interaction
import org.bukkit.Location
import java.util.*

data class NPCData(
    var id: String,
    var name: String,
    var uuid: UUID = UUID.randomUUID(),
    var location: Location,
    var skin: SkinResult = SkinResult(),
    var interaction: Interaction? = null,
)