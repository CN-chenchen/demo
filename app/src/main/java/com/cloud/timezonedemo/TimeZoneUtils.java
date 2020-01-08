package com.cloud.timezonedemo;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeZoneUtils {

    public static long getZeroStartTime(int day, long endUtcTimeMills) {
        TimeZone timeZone = TimeZone.getTimeZone("America/Chicago");

        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(endUtcTimeMills);

        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - (--day));
        long fromUtcTimeInMillis = calendar.getTimeInMillis();

        boolean inDaylightTime = timeZone.inDaylightTime(new Date(fromUtcTimeInMillis));
        long dayTimeMills = 24 * 60 * 60 * 1000;

        if (inDaylightTime) {
            boolean frontDaylightTime = timeZone.
                    inDaylightTime(new Date(fromUtcTimeInMillis - dayTimeMills));
            boolean behindDaylightTime = timeZone.
                    inDaylightTime(new Date(fromUtcTimeInMillis + dayTimeMills));

            if (!frontDaylightTime & behindDaylightTime) {
                dayTimeMills = 24 * 60 * 60 * 1000 - timeZone.getDSTSavings();
            }
            if (!behindDaylightTime && frontDaylightTime) {
                dayTimeMills = 24 * 60 * 60 * 1000 + timeZone.getDSTSavings();
            }
        }

        long fleetOffset = timeZone.getOffset(fromUtcTimeInMillis);
        long fleetTimeMills = fromUtcTimeInMillis + fleetOffset;

        return fleetTimeMills - fleetTimeMills % dayTimeMills - fleetOffset;
    }

    public static long[] getTimeOfDay(long startTimeMills, long endTimeMills) {
        TimeZone timeZone = TimeZone.getTimeZone("America/Chicago");
        long[] longs = new long[2];

        boolean fromInDaylightTime = timeZone.inDaylightTime(new Date(startTimeMills));
        boolean toInDaylightTime = timeZone.inDaylightTime(new Date(endTimeMills));
        long dayTimeMills = 24 * 60 * 60 * 1000;

        if (fromInDaylightTime && toInDaylightTime) {
            boolean frontDaylight = timeZone.
                    inDaylightTime(new Date(startTimeMills - dayTimeMills));
            boolean behindDaylight = timeZone.
                    inDaylightTime(new Date(endTimeMills + 1));

            if (!behindDaylight && frontDaylight) {
                longs[0] = endTimeMills + 1 - dayTimeMills;
                longs[1] = dayTimeMills + timeZone.getDSTSavings();
            }
        } else if (fromInDaylightTime) {
            for (long l = startTimeMills; l < endTimeMills; ) {
                l += dayTimeMills;
                boolean inDaylightTime = timeZone.inDaylightTime(new Date(l));
                if (!inDaylightTime) {
                    longs[0] = l - dayTimeMills;
                    longs[1] = dayTimeMills + timeZone.getDSTSavings();
                    break;
                }
            }
        } else if (toInDaylightTime) {
            for (long l = startTimeMills; l < endTimeMills; ) {
                l += dayTimeMills;
                boolean inDaylightTime = timeZone.inDaylightTime(new Date(l));
                if (inDaylightTime) {
                    longs[0] = l - dayTimeMills;
                    longs[1] = dayTimeMills - timeZone.getDSTSavings();
                    break;
                }
            }
        }
        return longs;
    }

}
