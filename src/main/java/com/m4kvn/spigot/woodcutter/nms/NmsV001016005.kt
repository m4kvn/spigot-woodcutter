package com.m4kvn.spigot.woodcutter.nms

import net.minecraft.server.v1_16_R3.BlockPosition
import net.minecraft.server.v1_16_R3.Blocks
import net.minecraft.server.v1_16_R3.ItemStack
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import net.minecraft.server.v1_16_R3.Block as NMSBlock

class NmsV001016005 : NMS {

    override fun breakLeaves(player: Player, block: Block) {
        val craftBlock = block as CraftBlock
        val location = player.location
        val dropPosition = BlockPosition(location.x, location.y, location.z)
        val world = craftBlock.craftWorld.handle
        val tileEntity = world.getTileEntity(craftBlock.position)
        val iBlockData = craftBlock.nms
        if (iBlockData.block != Blocks.AIR && (!iBlockData.isRequiresSpecialTool)) {
            NMSBlock.dropItems(craftBlock.nms, world, dropPosition, tileEntity, null, ItemStack.b)
        }
        craftBlock.setTypeAndData(Blocks.AIR.blockData, true)
    }

    override fun breakBlockByPlayer(player: Player, block: Block) {
        val craftPlayer = player as CraftPlayer
        val blockPosition = BlockPosition(block.x, block.y, block.z)
        craftPlayer.handle.playerInteractManager.breakBlock(blockPosition)
    }
}