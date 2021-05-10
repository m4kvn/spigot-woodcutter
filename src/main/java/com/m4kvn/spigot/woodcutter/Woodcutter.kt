package com.m4kvn.spigot.woodcutter

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin

class Woodcutter : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        server.pluginManager.registerEvents(object : Listener {
            @EventHandler(priority = EventPriority.MONITOR)
            fun onBlockBreak(event: BlockBreakEvent) {
                if (event.isCancelled) return
                Bukkit.broadcastMessage("event=${event.block.blockData.asString}")
            }
        }, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}