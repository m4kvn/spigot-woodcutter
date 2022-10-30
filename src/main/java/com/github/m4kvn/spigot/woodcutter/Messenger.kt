package com.github.m4kvn.spigot.woodcutter

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Messenger(
    private val plugin: JavaPlugin,
    private val chatColor: ChatColor,
) {

    private fun prefix(obj: Any) = "${chatColor}[${plugin.name}]${ChatColor.RESET} $obj"

    fun log(obj: Any) {
        Bukkit.getServer().consoleSender.sendMessage(prefix(obj))
    }

    fun send(player: Player, obj: Any) {
        player.sendMessage(prefix(obj))
    }

    companion object {

        fun instance(
            plugin: JavaPlugin,
            chatColor: ChatColor = ChatColor.RESET,
        ) = lazy {
            Messenger(plugin, chatColor)
        }
    }
}