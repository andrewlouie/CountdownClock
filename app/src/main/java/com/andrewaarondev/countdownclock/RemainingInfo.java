package com.andrewaarondev.countdownclock;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Calendar;


public class RemainingInfo {
    private Period period;

    RemainingInfo(Calendar dateFrom,Calendar dateTo) {
        dateTo.set(Calendar.SECOND, 0);
        dateTo.set(Calendar.MILLISECOND, 0);
        DateTime dateTimeFrom = new DateTime(dateFrom);
        DateTime dateTimeTo = new DateTime(dateTo);
        this.period = new Period(dateTimeFrom,dateTimeTo);
    }

    RemainingInfo(Calendar dateFrom, Calendar dateTo, boolean noMonth) {
        dateTo.set(Calendar.SECOND, 0);
        dateTo.set(Calendar.MILLISECOND, 0);
        DateTime dateTimeFrom = new DateTime(dateFrom);
        DateTime dateTimeTo = new DateTime(dateTo);
        this.period = new Period(dateTimeFrom, dateTimeTo, PeriodType.yearWeekDayTime());
    }
    public int getYears() {
        PeriodFormatter formatter = new PeriodFormatterBuilder().appendYears().toFormatter();
        return tryParse(formatter.print(period));
    }
    public int getMonths() {
        PeriodFormatter formatter = new PeriodFormatterBuilder().appendMonths().toFormatter();
        return tryParse(formatter.print(period));
    }
    public int getWeeks() {
        PeriodFormatter formatter = new PeriodFormatterBuilder().appendWeeks().toFormatter();
        return tryParse(formatter.print(period));
    }
    public int getDays() {
        PeriodFormatter formatter = new PeriodFormatterBuilder().appendDays().toFormatter();
        return tryParse(formatter.print(period));
    }
    public int getHours() {
        PeriodFormatter formatter = new PeriodFormatterBuilder().appendHours().toFormatter();
        return tryParse(formatter.print(period));
    }
    public int getMinutes() {
        PeriodFormatter formatter = new PeriodFormatterBuilder().appendMinutes().toFormatter();
        return tryParse(formatter.print(period));
    }
    public int getSeconds() {
        PeriodFormatter formatter = new PeriodFormatterBuilder().appendSeconds().toFormatter();
        return tryParse(formatter.print(period));
    }
    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
