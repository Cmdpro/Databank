package com.cmdpro.databank.events.builtin;

import com.cmdpro.databank.events.TimeDatabankEvent;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;

public class ThanksgivingEvent extends TimeDatabankEvent {
    @Override
    protected Timespan getTimespan() {
        LocalDate now = LocalDate.now();
        int firstDay = Month.NOVEMBER.firstDayOfYear(now.isLeapYear());
        LocalDate day = LocalDate.ofYearDay(now.getYear(), firstDay).with(TemporalAdjusters.next(DayOfWeek.THURSDAY)).plusWeeks(3);
        return Timespan.createTimespan(day.getMonthValue(), day.getDayOfMonth());
    }
}
