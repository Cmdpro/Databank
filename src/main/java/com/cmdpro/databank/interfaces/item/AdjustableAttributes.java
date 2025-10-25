package com.cmdpro.databank.interfaces.item;

import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public interface AdjustableAttributes {

    /**
     * Called whenever ItemAttributeModifierEvent fires, for altering attributes etc.
     */
    void adjustAttributes(ItemAttributeModifierEvent event);
}