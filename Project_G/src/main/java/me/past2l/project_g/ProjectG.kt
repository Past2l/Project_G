package me.past2l.project_g

import me.past2l.project_g.entity.NPC
import me.past2l.project_g.entity.Player
import me.past2l.project_g.event.*
import me.past2l.project_g.gui.Scoreboard
import me.past2l.project_g.gui.TabList
import me.past2l.project_g.nms.NMS
import me.past2l.project_g.packet.Packet
import me.past2l.project_g.scheduler.GUILoadScheduler
import me.past2l.project_g.scheduler.NPCSkinReloadScheduler
import me.past2l.project_g.scheduler.StaminaScheduler
import me.past2l.project_g.command.*
import me.past2l.project_g.config.Config
import me.past2l.project_g.config.TextConfig
import me.past2l.project_g.entity.CustomNPC
import me.past2l.project_g.gui.CustomGUI
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.plugin.java.JavaPlugin

class ProjectG : JavaPlugin() {
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
        UserInfoEvent(),
        StaminaEvent(),
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
        this.initConfigs()
        this.saveConfigs()
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

    private fun initConfigs() {
        Config.init()
        TextConfig.init()
    }

    private fun saveConfigs() {
        Config.save()
        TextConfig.save()
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
        StaminaScheduler.init()
    }

    private fun removeSchedulers() {
        GUILoadScheduler.remove()
        NPCSkinReloadScheduler.remove()
        StaminaScheduler.remove()
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