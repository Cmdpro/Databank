package com.cmdpro.databank.specialconditions;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public abstract class TimeDatabankSpecialCondition extends DatabankSpecialCondition {
    private int distance;
    @Override
    public boolean checkActive() {
        LocalDate now = LocalDate.now();
        Timespan timespan = getTimespan();
        distance = timespan.getDistance(now);
        return isValidYear(now.getYear()) && distance <= -1;
    }

    @Override
    public boolean isActiveParams(Map<String, Object> params) {
        if (params.containsKey("distance")) {
            if (params.get("distance") instanceof Integer distance) {
                return getDistance() <= distance;
            }
            return false;
        }
        return super.isActiveParams(params);
    }

    public int getDistance() {
        return distance;
    }
    public boolean isValidYear(int year) {
        return true;
    }
    protected abstract Timespan getTimespan();
    public record Timespan(MonthDay from, MonthDay to) {
        public boolean isActive(LocalDate date) {
            MonthDay monthDay = MonthDay.from(date);
            return !monthDay.isBefore(from) && !monthDay.isAfter(to);
        }
        public int getDistance(LocalDate date) {
            if (isActive(date)) {
                return -1;
            }

            int year = date.getYear();
            int fromDist = Math.clamp(ChronoUnit.DAYS.between(date, from.atYear(year)), 0, Integer.MAX_VALUE);
            int toDist = Math.clamp(-ChronoUnit.DAYS.between(date, to.atYear(year)), 0, Integer.MAX_VALUE);
            return Math.min(fromDist, toDist);
        }
        public static Timespan createTimespan(MonthDay from, MonthDay to) {
            return new Timespan(from, to);
        }
        public static Timespan createTimespan(int fromMonth, int fromDay, int toMonth, int toDay) {
            return createTimespan(MonthDay.of(fromMonth, fromDay), MonthDay.of(toMonth, toDay));
        }
        public static Timespan createTimespan(int month, int day) {
            return createTimespan(MonthDay.of(month, day), MonthDay.of(month, day));
        }
        public static Timespan createTimespan(Month fromMonth, int fromDay, Month toMonth, int toDay) {
            return createTimespan(MonthDay.of(fromMonth.getValue(), fromDay), MonthDay.of(toMonth.getValue(), toDay));
        }
        public static Timespan createTimespan(Month month, int day) {
            MonthDay finalDay = MonthDay.of(month.getValue(), day);
            return createTimespan(finalDay, finalDay);
        }
        public static Timespan createTimespan(Month month) {
            return createTimespan(month, month);
        }
        public static Timespan createTimespan(Month fromMonth, Month toMonth) {
            int length = toMonth.length(LocalDate.now().isLeapYear());
            MonthDay from = MonthDay.of(fromMonth.getValue(), 1);
            MonthDay to = MonthDay.of(toMonth.getValue(), length);
            return createTimespan(from, to);
        }
    }
}
