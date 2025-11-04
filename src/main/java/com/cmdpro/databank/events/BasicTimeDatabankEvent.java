package com.cmdpro.databank.events;

public class BasicTimeDatabankEvent extends TimeDatabankEvent {
    public Timespan timespan;
    public BasicTimeDatabankEvent(Timespan timespan) {
        this.timespan = timespan;
    }
    @Override
    protected Timespan getTimespan() {
        return timespan;
    }
}
