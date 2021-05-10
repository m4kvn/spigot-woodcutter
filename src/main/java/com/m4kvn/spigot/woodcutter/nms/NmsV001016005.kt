package com.m4kvn.spigot.woodcutter.nms

import net.minecraft.server.v1_16_R3.BlockPosition
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Player

class NmsV001016005 : NMS {

    override fun callBlockBreakEvent(player: Player, block: Block) {
        val craftPlayer = player as CraftPlayer
        val blockPosition = BlockPosition(block.x, block.y, block.z)
        craftPlayer.handle.playerInteractManager.breakBlock(blockPosition)
    }
}