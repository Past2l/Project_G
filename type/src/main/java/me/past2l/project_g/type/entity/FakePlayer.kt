package me.past2l.project_g.type.entity

import me.past2l.project_g.type.skin.SkinResult
import org.bukkit.Location
import java.util.*

data class FakePlayer(
    var id: String,
    var name: String,
    var uuid: UUID = UUID.randomUUID(),
    var location: Location,
    var skin: SkinResult = SkinResult(),
)
