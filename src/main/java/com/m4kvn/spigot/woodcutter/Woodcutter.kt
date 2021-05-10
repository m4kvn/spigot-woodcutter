package com.m4kvn.spigot.woodcutter

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.Statistic
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

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

        val isBroken = itemInMainHand.damage(checkedBlocks.size)
        if (isBroken) {
            event.player.breakItemInMainHand()
        }

        event.player.incrementStatistic(Statistic.MINE_BLOCK, event.block.type, checkedBlocks.size)
        event.player.incrementStatistic(Statistic.USE_ITEM, itemInMainHand.type, checkedBlocks.size)
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {}

    private fun Player.breakItemInMainHand() {
        world.playSound(location, Sound.ENTITY_ITEM_BREAK, 1f, 1f)
        inventory.setItemInMainHand(ItemStack(Material.AIR))
    }

    private fun ItemStack.calcDamage(amount: Int): Int {
        var damage = amount
        if (containsEnchantment(Enchantment.DURABILITY)) {
            val level = getEnchantmentLevel(Enchantment.DURABILITY)
            repeat(amount) {
                damage -= if (Random.nextInt(level) == 0) 0 else 1
            }
        }
        return damage
    }

    private fun ItemStack.damage(amount: Int): Boolean {
        val damageable = itemMeta as? Damageable ?: return false
        if ((damageable as ItemMeta).isUnbreakable) return false
        val damage = calcDamage(amount)
        damageable.damage = damageable.damage + damage
        itemMeta = damageable
        return damageable.damage >= type.maxDurability
    }

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
}