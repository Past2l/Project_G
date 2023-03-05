package me.past2l.api.type.player

import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

data class PlayerData(
    var nickname: String,
    var uuid: UUID,
    var prefix: String = "",
    var money: Double = 0.0,
    var cash: Double = 0.0,
    var like: Double = 0.0,
    var likeEnable: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")),
    var playtime: Double = 0.0,
    var lastPlayed: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")),
    var stamina: Int = 240,
)