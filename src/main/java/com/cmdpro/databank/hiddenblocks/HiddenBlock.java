package com.cmdpro.databank.hiddenblocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class HiddenBlock {
    public HiddenBlock(HiddenBlockConditions.HiddenBlockCondition condition, Block originalBlock, BlockState hiddenAs) {
        this.condition = condition;
        this.originalBlock = originalBlock;
        this.hiddenAs = hiddenAs;
    }
    public HiddenBlockConditions.HiddenBlockCondition condition;
    public Block originalBlock;
    public BlockState hiddenAs;
}