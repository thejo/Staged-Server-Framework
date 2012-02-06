/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Thejo
 */
public class DateTimeUtilities {
    /**
     * Returns the current date according to the required format
     *
     * @param strFormat The format in which the date is required
     * @return Today's date as a string
     */
    public static String getDateAsString(String strFormat) {

        return getDateAsString(strFormat, System.currentTimeMillis());
    }

    public static String getDateAsString(String strFormat, long epochTime) {

        Date now = new Date(epochTime);

        SimpleDateFormat format = new SimpleDateFormat(strFormat);

        return format.format(now);
    }

    /**
     * Determine the hour of day when the method is called
     *
     * @return int in the range (0-23)
     */
    public static int getHourOfDay() {

        return Integer.parseInt( getDateAsString("H") );
    }

    /**
     * Return the minute of day when the method is called
     *
     * @return Minutes elapsed in the current day
     */
    public static int getMinuteOfDay() {

        return (getHourOfDay() * 60) + Integer.parseInt( getDateAsString("m") );
    }

    public static int getSecondfOfDay() {

        return (getMinuteOfDay() * 60) +
                Integer.parseInt( getDateAsString("s") );
    }

    /**
     * Return the number of minutes till midnight
     *
     * @return
     */
    public static int getMinutesToMidnight() {

        return (24 * 60) - getMinuteOfDay();
    }

    /**
     * Return the number of seconds to midnight
     *
     * @return
     */
    public static int getSecondsToMidnight() {

        return (24 * 60 * 60) - getSecondfOfDay();
    }

    public static boolean isDateStringValid(String date, String format) {
        try {
          SimpleDateFormat sdf = new SimpleDateFormat(format);
          sdf.setLenient(false);
          sdf.parse(date);
        }
        catch (ParseException e) {
          return false;
        }
        catch (IllegalArgumentException e) {
          return false;
        }
        return true;
    }
}
