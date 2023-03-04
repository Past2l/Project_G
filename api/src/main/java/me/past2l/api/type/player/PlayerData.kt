package me.past2l.api.type.player

import java.time.ZoneId
import java.time.ZonedDateTime

data class PlayerData(
    var prefix: String = "",
    var money: Double = 0.0,
    var cash: Double = 0.0,
    var like: Double = 0.0,
    var playtime: Double = 0.0,
    var lastPlayed: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
)
