package com.m4kvn.spigot.woodcutter.nms

import net.minecraft.server.v1_16_R3.*
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
            val drops = NMSBlock.getDrops(iBlockData, world, dropPosition, tileEntity, null, ItemStack.b)
            drops.forEach { itemStack ->
                val d0 = (world.random.nextFloat() * 0.5f).toDouble() + 0.25
                val d1 = (world.random.nextFloat() * 0.5f).toDouble() + 0.25
                val d2 = (world.random.nextFloat() * 0.5f).toDouble() + 0.25
                if (!world.isClientSide && !itemStack.isEmpty && world.gameRules.getBoolean(GameRules.DO_TILE_DROPS)) {
                    val entityItem = EntityItem(world,
                        dropPosition.x + d0,
                        dropPosition.y + d1,
                        dropPosition.z + d2,
                        itemStack
                    )
                    entityItem.n()
                    if (world.captureDrops != null) {
                        world.captureDrops.add(entityItem)
                    } else {
                        world.addEntity(entityItem)
                    }
                }
            }
            iBlockData.dropNaturally(world, dropPosition, ItemStack.b)
        }
        craftBlock.setTypeAndData(Blocks.AIR.blockData, true)
    }

    override fun breakBlockByPlayer(player: Player, block: Block) {
        val craftPlayer = player as CraftPlayer
        val blockPosition = BlockPosition(block.x, block.y, block.z)
        craftPlayer.handle.playerInteractManager.breakBlock(blockPosition)
    }
}