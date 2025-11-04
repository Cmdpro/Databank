package com.cmdpro.databank.events;

public abstract class DatabankEvent {
    public boolean isActive;
    public abstract boolean checkActive();
}
