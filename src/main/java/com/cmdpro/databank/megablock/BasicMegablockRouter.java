package com.cmdpro.databank.megablock;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class BasicMegablockRouter extends MegablockRouter {
    public Supplier<Block> core;
    public BasicMegablockRouter(Properties properties, Supplier<Block> core) {
        super(properties);
        this.core = core;
    }

    @Override
    public Block getCore() {
        return core.get();
    }
}
