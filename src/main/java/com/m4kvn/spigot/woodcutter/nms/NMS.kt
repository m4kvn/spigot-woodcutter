package com.m4kvn.spigot.woodcutter.nms

import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface NMS {
    fun dropItemsOnPlayerLocation(player: Player, itemStack: ItemStack)
    fun breakLogs(player: Player, block: Block)
    fun breakLeaves(player: Player, block: Block)
}