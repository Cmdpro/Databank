package com.cmdpro.databank.events;

import java.util.ArrayList;
import java.util.List;

public class DatabankEventManager {
    protected static List<DatabankEvent> events = new ArrayList<>();
    public static void init() {
        for (DatabankEvent i : events) {
            i.isActive = i.checkActive();
        }
    }
    public static void addEvent(DatabankEvent event) {
        if (!events.contains(event)) {
            events.add(event);
        }
    }
    public static void removeEvent(DatabankEvent event) {
        events.remove(event);
    }
}
