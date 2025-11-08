package com.cmdpro.databank.specialconditions.builtin;

import com.cmdpro.databank.specialconditions.TimeDatabankSpecialCondition;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

public class ThanksgivingSpecialCondition extends TimeDatabankSpecialCondition {
    @Override
    protected Timespan getTimespan() {
        LocalDate now = LocalDate.now();
        int firstDay = Month.NOVEMBER.firstDayOfYear(now.isLeapYear());
        LocalDate day = LocalDate.ofYearDay(now.getYear(), firstDay).with(TemporalAdjusters.next(DayOfWeek.THURSDAY)).plusWeeks(3);
        return Timespan.createTimespan(day.getMonthValue(), day.getDayOfMonth());
    }
}
