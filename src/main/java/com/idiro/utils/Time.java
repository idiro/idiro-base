/** 
 *  Copyright © 2016 Red Sqirl, Ltd. All rights reserved.
 *  Red Sqirl, Clarendon House, 34 Clarendon St., Dublin 2. Ireland
 *
 *  This file is part of Utility Library developed by Idiro
 *
 *  User agrees that use of this software is governed by: 
 *  (1) the applicable user limitations and specified terms and conditions of 
 *      the license agreement which has been entered into with Red Sqirl; and 
 *  (2) the proprietary and restricted rights notices included in this software.
 *  
 *  WARNING: THE PROPRIETARY INFORMATION OF Utility Library developed by Idiro IS PROTECTED BY IRISH AND 
 *  INTERNATIONAL LAW.  UNAUTHORISED REPRODUCTION, DISTRIBUTION OR ANY PORTION
 *  OF IT, MAY RESULT IN CIVIL AND/OR CRIMINAL PENALTIES.
 *  
 *  If you have received this software in error please contact Red Sqirl at 
 *  support@redsqirl.com
 */


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
