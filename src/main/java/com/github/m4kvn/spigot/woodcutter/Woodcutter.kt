package com.github.m4kvn.spigot.woodcutter

import com.github.m4kvn.spigotnms.Nms
import com.github.m4kvn.spigotnms.nms
import org.bukkit.ChatColor
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

@Suppress("Unused")
class Woodcutter : JavaPlugin(), Listener {
    private val nms: Nms by nms()
    private val messenger by Messenger.instance(this, ChatColor.GREEN)

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

        val tree = event.block.asTree ?: return

        tree.logBlocks.forEach { block ->
            val metadataValue = FixedMetadataValue(this, tree)
            block.setMetadata(metadataKey, metadataValue)
            if (block.drops.isNotEmpty()) {
                val metadataDropValue = FixedMetadataValue(this, null)
                block.setMetadata(event.player.metadataKeyDrop, metadataDropValue)
            }
            nms.breakBlock(event.player, block)
            block.removeMetadata(metadataKey, this)
        }

        tree.leaves.forEach { leaves ->
            nms.breakLeaves(event.player, leaves)
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
                nms.dropItemStack(event.player, item.itemStack)
            }
        }
    }

    override fun onEnable() {
        messenger.log("onEnable")
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        messenger.log("onDisable")
    }

    private val Player.metadataKey: String
        get() = "${this@Woodcutter.name}_${name}"

    private val Player.metadataKeyDrop: String
        get() = "${this@Woodcutter.name}_${name}_drop"

    private val Block.isLog: Boolean
        get() = when (type) {
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

    private val Block.asTree: Tree?
        get() = when (type) {
            Material.SPRUCE_LOG -> Tree.SpruceTree(this)
            Material.OAK_LOG -> Tree.OakTree(this)
            Material.JUNGLE_LOG -> Tree.JungleTree(this)
            Material.DARK_OAK_LOG -> Tree.DarkOakTree(this)
            Material.BIRCH_LOG -> Tree.BirchTree(this)
            Material.ACACIA_LOG -> Tree.AcaciaTree(this)
            Material.CRIMSON_STEM -> Tree.CrimsonStemTree(this)
            Material.WARPED_STEM -> Tree.WarpedStemTree(this)
            else -> null
        }
}