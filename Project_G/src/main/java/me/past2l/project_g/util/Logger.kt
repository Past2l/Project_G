package me.past2l.project_g.util

import me.past2l.project_g.config.Config
import org.bukkit.Bukkit

class Logger {
    companion object {
        fun log(str: Any) = Bukkit.getConsoleSender()
            .sendMessage(Config.format("${Config.consolePrefix}: $str&r"))
        fun warn(str: String) = log("&e$str")
        fun error(str: String) = log("&c$str")
    }
}