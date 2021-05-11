package com.m4kvn.spigot.woodcutter

import org.bukkit.Material
import org.bukkit.block.Block

sealed class Tree {
    val brokenBlocks: MutableSet<Block> = mutableSetOf()
    abstract val firstBrokenLog: Block
    abstract val logMaterial: Material
    abstract val leavesMaterials: Set<Material>

    val logBlocks: Set<Block> by lazy {
        val unCheckedBlocks = mutableSetOf(firstBrokenLog)
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
        checkedBlocks
    }

    val leaves: List<Block> by lazy {
//        val checkedBlocks = mutableSetOf<Block>()
//        logBlocks.forEach { log ->
//            val relativeLeaves = log.getRelativeBlocks(3)
//                .filter { leavesMaterials.contains(it.type) }
//                .filterNot { checkedBlocks.contains(it) }
//            checkedBlocks.addAll(relativeLeaves)
//        }
//        checkedBlocks
        logBlocks
            .flatMap { it.getRelativeBlocks(3) }
            .filter { leavesMaterials.contains(it.type) }
            .distinct()
    }

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

    private fun Block.isSameLog(block: Block): Boolean =
        block.blockData.material == blockData.material
}

data class WarpedStemTree(
    override val firstBrokenLog: Block,
    override val logMaterial: Material = Material.WARPED_STEM,
    override val leavesMaterials: Set<Material> = setOf(),
) : Tree()

data class CrimsonStemTree(
    override val firstBrokenLog: Block,
    override val logMaterial: Material = Material.CRIMSON_STEM,
    override val leavesMaterials: Set<Material> = setOf(),
) : Tree()

data class AcaciaTree(
    override val firstBrokenLog: Block,
    override val logMaterial: Material = Material.DARK_OAK_LOG,
    override val leavesMaterials: Set<Material> = setOf(Material.ACACIA_LEAVES),
) : Tree()

data class BirchTree(
    override val firstBrokenLog: Block,
    override val logMaterial: Material = Material.BIRCH_LOG,
    override val leavesMaterials: Set<Material> = setOf(Material.BIRCH_LEAVES),
) : Tree()

data class DarkOakTree(
    override val firstBrokenLog: Block,
    override val logMaterial: Material = Material.DARK_OAK_LOG,
    override val leavesMaterials: Set<Material> = setOf(Material.DARK_OAK_LEAVES),
) : Tree()

data class JungleTree(
    override val firstBrokenLog: Block,
    override val logMaterial: Material = Material.JUNGLE_LOG,
    override val leavesMaterials: Set<Material> = setOf(Material.JUNGLE_LEAVES),
) : Tree()

data class OakTree(
    override val firstBrokenLog: Block,
    override val logMaterial: Material = Material.OAK_LOG,
    override val leavesMaterials: Set<Material> = setOf(Material.OAK_LEAVES),
) : Tree()

data class SpruceTree(
    override val firstBrokenLog: Block,
    override val logMaterial: Material = Material.SPRUCE_LOG,
    override val leavesMaterials: Set<Material> = setOf(Material.SPRUCE_LEAVES),
) : Tree()