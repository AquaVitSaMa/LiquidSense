package net.ccbluex.liquidbounce.utils.block;

import net.minecraft.block.Block;

public interface Collidable {

    /**
     * Check if [block] is collidable
     */
    boolean collideBlock(Block block);
}
