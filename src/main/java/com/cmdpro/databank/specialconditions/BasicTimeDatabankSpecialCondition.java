package com.cmdpro.databank.specialconditions;

public class BasicTimeDatabankSpecialCondition extends TimeDatabankSpecialCondition {
    public Timespan timespan;
    public BasicTimeDatabankSpecialCondition(Timespan timespan) {
        this.timespan = timespan;
    }
    @Override
    protected Timespan getTimespan() {
        return timespan;
    }
}
