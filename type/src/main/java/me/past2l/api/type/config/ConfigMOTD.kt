package me.past2l.api.type.config

data class ConfigMOTD(
    val content: ArrayList<String> = arrayListOf("%server.name%", ""),
    val center: Boolean = false,
)
