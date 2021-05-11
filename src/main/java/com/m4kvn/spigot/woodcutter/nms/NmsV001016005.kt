package com.m4kvn.spigot.woodcutter.nms

import net.minecraft.server.v1_16_R3.*
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import net.minecraft.server.v1_16_R3.Block as NMSBlock
import net.minecraft.server.v1_16_R3.ItemStack as NMSItemStack

class NmsV001016005 : NMS {

    private val Player.position: BlockPosition
        get() = BlockPosition(location.x, location.y, location.z)

    private fun createEntityItem(
        world: WorldServer,
        dropPosition: BlockPosition,
        craftItemStack: NMSItemStack,
    ): EntityItem {
        val d0 = (world.random.nextFloat() * 0.5f).toDouble() + 0.25
        val d1 = (world.random.nextFloat() * 0.5f).toDouble() + 0.25
        val d2 = (world.random.nextFloat() * 0.5f).toDouble() + 0.25
        val entityItem = EntityItem(
            world,
            dropPosition.x + d0,
            dropPosition.y + d1,
            dropPosition.z + d2,
            craftItemStack
        )
        entityItem.n()
        return entityItem
    }

    override fun dropItemsOnPlayerLocation(player: Player, itemStack: ItemStack) {
        val craftItemStack = CraftItemStack.asNMSCopy(itemStack)
        val world = (player.world as CraftWorld).handle
        if (canDrop(world, craftItemStack)) {
            val entityItem = createEntityItem(world, player.position, craftItemStack)
            world.addEntity(entityItem)
        }
    }

    override fun breakLeaves(player: Player, block: Block) {
        val craftBlock = block as CraftBlock
        val dropPosition = player.position
        val world = craftBlock.craftWorld.handle
        val tileEntity = world.getTileEntity(craftBlock.position)
        val iBlockData = craftBlock.nms
        if (iBlockData.block != Blocks.AIR && (!iBlockData.isRequiresSpecialTool)) {
            val drops = NMSBlock.getDrops(iBlockData, world, dropPosition, tileEntity, null, NMSItemStack.b)
            drops
                .filter { canDrop(world, it) }
                .forEach { itemStack ->
                    val entityItem = createEntityItem(world, dropPosition, itemStack)
                    if (world.captureDrops != null) {
                        world.captureDrops.add(entityItem)
                    } else {
                        world.addEntity(entityItem)
                    }
                }
            iBlockData.dropNaturally(world, dropPosition, NMSItemStack.b)
        }
        craftBlock.setTypeAndData(Blocks.AIR.blockData, true)
    }

    private fun canDrop(
        world: WorldServer,
        craftItemStack: net.minecraft.server.v1_16_R3.ItemStack
    ) = (!world.isClientSide
            && !craftItemStack.isEmpty
            && world.gameRules.getBoolean(GameRules.DO_TILE_DROPS))

    override fun breakLogs(player: Player, block: Block) {
        val craftPlayer = player as CraftPlayer
        val blockPosition = BlockPosition(block.x, block.y, block.z)
        craftPlayer.handle.playerInteractManager.breakBlock(blockPosition)
    }
}