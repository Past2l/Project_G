package me.past2l.project_g

import me.past2l.api.PluginManager
import me.past2l.project_g.entity.CustomNPC
import me.past2l.api.entity.NPC
import me.past2l.api.entity.Player
import me.past2l.api.event.*
import me.past2l.api.gui.Scoreboard
import me.past2l.api.gui.TabList
import me.past2l.api.nms.NMS
import me.past2l.api.packet.Packet
import me.past2l.api.scheduler.NPCSkinReloadScheduler
import me.past2l.api.scheduler.GUILoadScheduler
import me.past2l.project_g.command.*
import me.past2l.project_g.util.Config
import me.past2l.project_g.gui.CustomGUI
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.plugin.java.JavaPlugin

class ProjectG: JavaPlugin() {
    private val commands = hashMapOf(
        TestCommand.name to TestCommand(),
        CustomNPCCommand.name to CustomNPCCommand(),
        CustomGUICommand.name to CustomGUICommand(),
        EnderChestGUICommand.name to EnderChestGUICommand(),
        AdminCommand.name to AdminCommand(),
        PlayerCommand.name to PlayerCommand(),
    )
    private val events = arrayOf(
        NPCEvent(),
        GUIEvent(),
        PlayerEvent(),
        PacketEvent(),
        MOTDEvent(),
    )
    private val gameRules = hashMapOf(
        "difficulty" to "normal",
        "announceAdvancements" to "false",
        "commandBlockOutPut" to "false",
        "doFireTick" to "false",
        "doMobSpawning" to "false",
        "spawnRadius" to "0",
        "disableRaids" to "false",
        "doInsomnia" to "false",
    )

    override fun onEnable() {
        PluginManager.init(this)
        Config.init()
        Config.save()
        if (!NMS.init()) return
        Player.loadData()
        Player.saveData()
        this.initGUIs()
        this.initNPCs()
        this.initEvents()
        this.initCommands()
        this.initGameRules()
        this.initSchedulers()
    }

    override fun onDisable() {
        this.closeGUIs()
        this.removeNPCs()
        this.removeEvents()
        this.removeSchedulers()
    }

    private fun initCommands() {
        commands.map {
            getCommand(it.key)?.executor = it.value
            getCommand(it.key)?.tabCompleter = it.value
        }
    }

    private fun initEvents() {
        Packet.setEvent { player, packet ->
            CustomNPC.onInteractNPC(player, packet)
        }
        Packet.init()
        events.map { server.pluginManager.registerEvents(it, this) }
    }

    private fun removeEvents() {
        Packet.remove()
    }

    private fun initGUIs() {
        TabList.setHeaderFooter()
        TabList.setPlayerName()
        Bukkit.getOnlinePlayers().forEach { Scoreboard.set(it) }
        CustomGUI.init()
    }

    private fun initGameRules() {
        Bukkit.getWorlds().forEach {
            it.difficulty = when (gameRules["difficulty"]?.lowercase()) {
                "peaceful" -> Difficulty.PEACEFUL
                "easy" -> Difficulty.EASY
                "normal" -> Difficulty.NORMAL
                "hard" -> Difficulty.HARD
                else -> Difficulty.NORMAL
            }
            gameRules.remove("difficulty")
            gameRules.map { v -> it.setGameRuleValue(v.key, v.value) }
        }
    }

    private fun initSchedulers() {
        GUILoadScheduler.init()
        NPCSkinReloadScheduler.init()
    }

    private fun removeSchedulers() {
        GUILoadScheduler.remove()
        NPCSkinReloadScheduler.remove()
    }

    private fun initNPCs() {
        CustomNPC.init()
    }

    private fun removeNPCs() {
        CustomNPC.remove()
        NPC.remove()
    }

    private fun closeGUIs() {
        Bukkit.getOnlinePlayers().forEach { Player.gui[it.uniqueId]?.close(it) }
    }
}