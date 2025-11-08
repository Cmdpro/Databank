package com.cmdpro.databank.specialconditions;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class DatabankSpecialConditionManager {
    protected static HashMap<ResourceLocation, DatabankSpecialCondition> events = new HashMap<>();
    public static void init() {
        for (DatabankSpecialCondition i : events.values()) {
            i.isActive = i.checkActive();
        }
    }
    public static void addEvent(ResourceLocation id, DatabankSpecialCondition event) {
        events.put(id, event);
        event.id = id;
    }
    public static void removeEvent(ResourceLocation id) {
        events.remove(id);
    }
    public static DatabankSpecialCondition getEvent(ResourceLocation id) {
        return events.get(id);
    }
}
