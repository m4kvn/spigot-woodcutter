package com.m4kvn.spigot.woodcutter

import com.m4kvn.spigot.woodcutter.nms.NmsV001016005
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin

class Woodcutter : JavaPlugin(), Listener {
    private val nms by lazy {
        when (server.bukkitVersion) {
            "1.16.5-R0.1-SNAPSHOT" -> NmsV001016005()
            else -> throw Exception()
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onLogBreak(event: BlockBreakEvent) {
        when {
            event.isCancelled -> return
            !event.block.isLog -> return
            event.player.isSneaking -> return
            !event.player.inventory.itemInMainHand.isAxe -> return
        }

        val metadataKey = event.player.metadataKey
        if (event.block.hasMetadata(metadataKey)) {
            val tree = event.block.getMetadata(metadataKey).first().value() as Tree
            tree.brokenBlocks.add(event.block)
            return
        }

        val tree = Tree(event.block)
        val unCheckedBlocks = mutableSetOf(event.block)
        val checkedBlocks = mutableSetOf<Block>()

        while (unCheckedBlocks.isNotEmpty()) {
            val checkingBlock = unCheckedBlocks.first()
            unCheckedBlocks.remove(checkingBlock)
            checkedBlocks.add(checkingBlock)
            val relativeBlocks = checkingBlock.getRelativeBlocks(1)
                .filter { it.isSameLog(checkingBlock) }
                .filterNot { checkedBlocks.contains(it) }
            unCheckedBlocks.addAll(relativeBlocks)
        }

        checkedBlocks.forEach { block ->
            val metadataValue = FixedMetadataValue(this, tree)
            block.setMetadata(metadataKey, metadataValue)
            if (block.drops.isNotEmpty()) {
                val metadataDropValue = FixedMetadataValue(this, null)
                block.setMetadata(event.player.metadataKeyDrop, metadataDropValue)
            }
            nms.callBlockBreakEvent(event.player, block)
            block.removeMetadata(metadataKey, this)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onBlockDropItem(event: BlockDropItemEvent) {
        val metadataKey = event.player.metadataKeyDrop
        if (event.block.hasMetadata(metadataKey)) {
            val items = event.items.toMutableList()
            event.items.clear()
            event.block.removeMetadata(metadataKey, this)
            items.forEach { item ->
                event.player.dropItem(item.itemStack)
            }
        }
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {}

    private val Player.metadataKey: String
        get() = "${this@Woodcutter.name}_${name}"

    private val Player.metadataKeyDrop: String
        get() = "${this@Woodcutter.name}_${name}_drop"

    private fun Player.dropItem(itemStack: ItemStack) {
        world.dropItem(location, itemStack)
    }

    private fun Block.isSameLog(block: Block): Boolean =
        block.blockData.material == blockData.material

    private fun Block.getRelativeBlocks(distance: Int): List<Block> {
        val blocks = mutableListOf<Block>()
        val range = -distance..distance
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
}