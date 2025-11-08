package com.cmdpro.databank.specialconditions;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.specialconditions.builtin.*;

import java.time.Month;

import static com.cmdpro.databank.specialconditions.TimeDatabankSpecialCondition.Timespan.createTimespan;

public class BuiltinDatabankSpecialConditions {
    public static final BasicTimeDatabankSpecialCondition aprilFools = new BasicTimeDatabankSpecialCondition(createTimespan(Month.APRIL, 1));
    public static final BasicTimeDatabankSpecialCondition christmas = new BasicTimeDatabankSpecialCondition(createTimespan(Month.DECEMBER, 25));
    public static final BasicTimeDatabankSpecialCondition halloween = new BasicTimeDatabankSpecialCondition(createTimespan(Month.OCTOBER, 31));
    public static final BasicTimeDatabankSpecialCondition independenceDay = new BasicTimeDatabankSpecialCondition(createTimespan(Month.JULY, 4));
    public static final BasicTimeDatabankSpecialCondition newYears = new BasicTimeDatabankSpecialCondition(createTimespan(Month.JANUARY, 1));
    public static final BasicTimeDatabankSpecialCondition prideMonth = new BasicTimeDatabankSpecialCondition(createTimespan(Month.JUNE));
    public static final ThanksgivingSpecialCondition thanksgiving = new ThanksgivingSpecialCondition();


    public static final BasicTimeDatabankSpecialCondition january = new BasicTimeDatabankSpecialCondition(createTimespan(Month.JANUARY));
    public static final BasicTimeDatabankSpecialCondition february = new BasicTimeDatabankSpecialCondition(createTimespan(Month.FEBRUARY));
    public static final BasicTimeDatabankSpecialCondition march = new BasicTimeDatabankSpecialCondition(createTimespan(Month.MARCH));
    public static final BasicTimeDatabankSpecialCondition april = new BasicTimeDatabankSpecialCondition(createTimespan(Month.APRIL));
    public static final BasicTimeDatabankSpecialCondition may = new BasicTimeDatabankSpecialCondition(createTimespan(Month.MAY));
    public static final BasicTimeDatabankSpecialCondition june = new BasicTimeDatabankSpecialCondition(createTimespan(Month.JUNE));
    public static final BasicTimeDatabankSpecialCondition july = new BasicTimeDatabankSpecialCondition(createTimespan(Month.JULY));
    public static final BasicTimeDatabankSpecialCondition august = new BasicTimeDatabankSpecialCondition(createTimespan(Month.AUGUST));
    public static final BasicTimeDatabankSpecialCondition september = new BasicTimeDatabankSpecialCondition(createTimespan(Month.SEPTEMBER));
    public static final BasicTimeDatabankSpecialCondition october = new BasicTimeDatabankSpecialCondition(createTimespan(Month.OCTOBER));
    public static final BasicTimeDatabankSpecialCondition november = new BasicTimeDatabankSpecialCondition(createTimespan(Month.NOVEMBER));
    public static final BasicTimeDatabankSpecialCondition december = new BasicTimeDatabankSpecialCondition(createTimespan(Month.DECEMBER));

    public static void init() {
        DatabankSpecialConditionManager.addEvent(Databank.locate("april_fools"), aprilFools);
        DatabankSpecialConditionManager.addEvent(Databank.locate("christmas"), christmas);
        DatabankSpecialConditionManager.addEvent(Databank.locate("halloween"), halloween);
        DatabankSpecialConditionManager.addEvent(Databank.locate("independence_day"), independenceDay);
        DatabankSpecialConditionManager.addEvent(Databank.locate("new_years"), newYears);
        DatabankSpecialConditionManager.addEvent(Databank.locate("pride_month"), prideMonth);
        DatabankSpecialConditionManager.addEvent(Databank.locate("thanksgiving"), thanksgiving);

        DatabankSpecialConditionManager.addEvent(Databank.locate("january"), january);
        DatabankSpecialConditionManager.addEvent(Databank.locate("february"), february);
        DatabankSpecialConditionManager.addEvent(Databank.locate("march"), march);
        DatabankSpecialConditionManager.addEvent(Databank.locate("april"), april);
        DatabankSpecialConditionManager.addEvent(Databank.locate("may"), may);
        DatabankSpecialConditionManager.addEvent(Databank.locate("june"), june);
        DatabankSpecialConditionManager.addEvent(Databank.locate("july"), july);
        DatabankSpecialConditionManager.addEvent(Databank.locate("august"), august);
        DatabankSpecialConditionManager.addEvent(Databank.locate("september"), september);
        DatabankSpecialConditionManager.addEvent(Databank.locate("october"), october);
        DatabankSpecialConditionManager.addEvent(Databank.locate("november"), november);
        DatabankSpecialConditionManager.addEvent(Databank.locate("december"), december);
    }
}
