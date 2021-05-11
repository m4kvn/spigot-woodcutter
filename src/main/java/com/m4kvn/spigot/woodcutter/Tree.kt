package com.m4kvn.spigot.woodcutter

import org.bukkit.block.Block

data class Tree(
    val block: Block,
    val brokenBlocks: MutableSet<Block> = mutableSetOf()
)