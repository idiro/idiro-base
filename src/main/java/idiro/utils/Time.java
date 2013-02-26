/*
 * Copyright 2009 by Idiro Technologies. 
 * All rights reserved
 */
package idiro.utils;

import java.util.concurrent.TimeUnit;

/**
 * Utilities class for performing Date functions.
 * 
 * @author Donal Doyle
 */
public final class Time {

    /**
     * Constructor.
     */
    private Time() {

    }

    /**
     * DAYS INDEX.
     */
    public static final int DAYS = 0;

    /**
     * HOURS INDEX.
     */
    public static final int HOURS = 1;

    /**
     * MINUTES INDEX.
     */
    public static final int MINUTES = 2;

    /**
     * SECONDS INDEX.
     */
    public static final int SECONDS = 3;

    /**
     * MILLIS INDEX.
     */
    public static final int MILLIS = 4;

    /**
     * @param milliseconds
     *            the number of milliseconds
     * @return The time split in hours minutes seconds and milliseconds
     */
    public static long[] getHMSm(final long milliseconds) {
        long[] result = new long[5];
        long ms = milliseconds;
        long days = TimeUnit.MILLISECONDS.toDays(ms);
        ms = ms - TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(ms);
        ms = ms - TimeUnit.HOURS.toMillis(hours);
        long mins = TimeUnit.MILLISECONDS.toMinutes(ms);
        ms = ms - TimeUnit.MINUTES.toMillis(mins);
        long secs = TimeUnit.MILLISECONDS.toSeconds(ms);
        ms = ms - TimeUnit.SECONDS.toMillis(secs);
        result[DAYS] = days;
        result[HOURS] = hours;
        result[MINUTES] = mins;
        result[SECONDS] = secs;
        result[MILLIS] = ms;
        return result;
    }

}
