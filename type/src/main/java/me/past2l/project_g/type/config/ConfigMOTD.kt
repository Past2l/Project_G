package me.past2l.project_g.type.config

data class ConfigMOTD(
    val content: ArrayList<String> = arrayListOf("%server.name%", ""),
    val center: Boolean = false,
)
