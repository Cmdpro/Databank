package com.cmdpro.databank.hidden;

import net.minecraft.resources.ResourceLocation;

public class Hidden {
    public Hidden(HiddenTypeInstance<?> type, HiddenCondition condition) {
        this.type = type;
        this.condition = condition;
        this.type.hidden = this;
    }
    public ResourceLocation id;
    public HiddenTypeInstance<?> type;
    public HiddenCondition condition;
}
