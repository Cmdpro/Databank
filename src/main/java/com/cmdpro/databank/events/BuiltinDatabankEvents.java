package com.cmdpro.databank.events;

import com.cmdpro.databank.events.builtin.*;

import java.time.Month;

import static com.cmdpro.databank.events.TimeDatabankEvent.Timespan.createTimespan;

public class BuiltinDatabankEvents {
    public static final BasicTimeDatabankEvent aprilFools = new BasicTimeDatabankEvent(createTimespan(Month.APRIL, 1));
    public static final BasicTimeDatabankEvent christmas = new BasicTimeDatabankEvent(createTimespan(Month.DECEMBER, 25));
    public static final BasicTimeDatabankEvent halloween = new BasicTimeDatabankEvent(createTimespan(Month.OCTOBER, 31));
    public static final BasicTimeDatabankEvent independenceDay = new BasicTimeDatabankEvent(createTimespan(Month.JULY, 4));
    public static final BasicTimeDatabankEvent newYears = new BasicTimeDatabankEvent(createTimespan(Month.JANUARY, 1));
    public static final BasicTimeDatabankEvent prideMonth = new BasicTimeDatabankEvent(createTimespan(Month.JUNE));
    public static final ThanksgivingEvent thanksgiving = new ThanksgivingEvent();


    public static final BasicTimeDatabankEvent january = new BasicTimeDatabankEvent(createTimespan(Month.JANUARY));
    public static final BasicTimeDatabankEvent february = new BasicTimeDatabankEvent(createTimespan(Month.FEBRUARY));
    public static final BasicTimeDatabankEvent march = new BasicTimeDatabankEvent(createTimespan(Month.MARCH));
    public static final BasicTimeDatabankEvent april = new BasicTimeDatabankEvent(createTimespan(Month.APRIL));
    public static final BasicTimeDatabankEvent may = new BasicTimeDatabankEvent(createTimespan(Month.MAY));
    public static final BasicTimeDatabankEvent june = new BasicTimeDatabankEvent(createTimespan(Month.JUNE));
    public static final BasicTimeDatabankEvent july = new BasicTimeDatabankEvent(createTimespan(Month.JULY));
    public static final BasicTimeDatabankEvent august = new BasicTimeDatabankEvent(createTimespan(Month.AUGUST));
    public static final BasicTimeDatabankEvent september = new BasicTimeDatabankEvent(createTimespan(Month.SEPTEMBER));
    public static final BasicTimeDatabankEvent october = new BasicTimeDatabankEvent(createTimespan(Month.OCTOBER));
    public static final BasicTimeDatabankEvent november = new BasicTimeDatabankEvent(createTimespan(Month.NOVEMBER));
    public static final BasicTimeDatabankEvent december = new BasicTimeDatabankEvent(createTimespan(Month.DECEMBER));

    public static void init() {
        DatabankEventManager.addEvent(aprilFools);
        DatabankEventManager.addEvent(christmas);
        DatabankEventManager.addEvent(halloween);
        DatabankEventManager.addEvent(independenceDay);
        DatabankEventManager.addEvent(newYears);
        DatabankEventManager.addEvent(prideMonth);
        DatabankEventManager.addEvent(thanksgiving);

        DatabankEventManager.addEvent(january);
        DatabankEventManager.addEvent(february);
        DatabankEventManager.addEvent(march);
        DatabankEventManager.addEvent(april);
        DatabankEventManager.addEvent(may);
        DatabankEventManager.addEvent(june);
        DatabankEventManager.addEvent(july);
        DatabankEventManager.addEvent(august);
        DatabankEventManager.addEvent(september);
        DatabankEventManager.addEvent(october);
        DatabankEventManager.addEvent(november);
        DatabankEventManager.addEvent(december);
    }
}
