package com.m4kvn.spigot.woodcutter

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class Woodcutter : JavaPlugin(), Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return
        if (!event.block.isLog) return
        if (!event.player.inventory.itemInMainHand.isAxe) return
        if (event.player.isSneaking) return

        val itemInMainHand = event.player.inventory.itemInMainHand
        val unCheckedBlocks = mutableSetOf(event.block)
        val checkedBlocks = mutableSetOf<Block>()

        while (unCheckedBlocks.isNotEmpty()) {
            val checkingBlock = unCheckedBlocks.first()
            unCheckedBlocks.remove(checkingBlock)
            checkedBlocks.add(checkingBlock)
            val relativeBlocks = checkingBlock.getRelativeBlocks()
                .filter { it.isSameLog(checkingBlock) }
                .filterNot { checkedBlocks.contains(it) }
            unCheckedBlocks.addAll(relativeBlocks)
        }

        checkedBlocks.forEach {
            it.breakNaturally(itemInMainHand)
        }
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {}

    private fun Block.isSameLog(block: Block): Boolean =
        block.blockData.material == blockData.material

    private fun Block.getRelativeBlocks(): List<Block> {
        val blocks = mutableListOf<Block>()
        val range = -1..1
        for (x in range) for (y in range) for (z in range) {
            if (x != 0 || y != 0 || z != 0) {
                blocks.add(getRelative(x, y, z))
            }
        }
        return blocks
    }

    private val Block.isLog: Boolean
        get() = when (blockData.material) {
            Material.WARPED_STEM,
            Material.CRIMSON_STEM,
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.DARK_OAK_LOG,
            Material.JUNGLE_LOG,
            Material.OAK_LOG,
            Material.SPRUCE_LOG -> true
            else -> false
        }

    private val ItemStack.isAxe: Boolean
        get() = when (type) {
            Material.DIAMOND_AXE,
            Material.GOLDEN_AXE,
            Material.IRON_AXE,
            Material.NETHERITE_AXE,
            Material.STONE_AXE,
            Material.WOODEN_AXE -> true
            else -> false
        }

    companion object {
        private const val METADATA_KEY = "woodcutter"
    }
}