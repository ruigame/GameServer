package com.game.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午2:48
 */
public class DateUtils {

    private static ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    private static ThreadLocal<SimpleDateFormat> shortsdf = ThreadLocal.withInitial(() -> new SimpleDateFormat("HH:mm:ss"));

    private static ThreadLocal<SimpleDateFormat> year_month_day = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    public static String format(Date d) {
        return sdf.get().format(d);
    }

    public static Date parse(String dateStr) {
        try {
            return sdf.get().parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseShort(String dateStr) {
        try {
            return shortsdf.get().parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseYearMonthDay(String dateStr) {
        try {
            return year_month_day.get().parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatYearMonthDay(Date date) {
        try {
            return year_month_day.get().format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String format(long timeMillis) {
        return format(new Date(timeMillis));
    }

    public static boolean isSameDay(long timeMillis) {
        return timeMillis > 0 && org.apache.commons.lang3.time.DateUtils.isSameDay(new Date(timeMillis), new Date());
    }

    public static boolean isSameDay(long timeMillis1, long timeMillis2) {
        return timeMillis1 > 0 && timeMillis2 > 0 && org.apache.commons.lang3.time.DateUtils.isSameDay(new Date(timeMillis1), new Date(timeMillis2));
    }

    /**
     * 指定时间戳的0点0分0秒的时间戳（毫秒）
     * @param milliTime
     * @return
     */
    private static long getDayStartMillis(long milliTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
