package com.game.util;

import java.util.Calendar;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 8:32 下午
 */
public class TimeUtils {

    public static final long TimeMillisOneHour = 24 * 60 * 60 * 1000;

    public static final long TimeMillisOneDay = 24 * 60 * 60 * 1000;

    public static final int SecondsTenMinute = 10 * 60;

    public final static int curTimestamp() {
        return (int)(System.currentTimeMillis() / 1000);
    }

    public final static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static boolean isSameDay(long timeMillis) {
        Calendar calendarBegin = Calendar.getInstance();
        calendarBegin.setTimeInMillis(timeMillis);
        return isSameDay(calendarBegin, Calendar.getInstance());
    }

    /**
     * 判断两个时间是否同一天
     * @param timeMillis1
     * @param timeMillis2
     * @return
     */
    public static boolean isSameDay(long timeMillis1, long timeMillis2) {
        Calendar calendarBegin = Calendar.getInstance();
        calendarBegin.setTimeInMillis(timeMillis1);
        Calendar calendarNow = Calendar.getInstance();
        calendarNow.setTimeInMillis(timeMillis2);
        return isSameDay(calendarBegin, calendarNow);
    }

    /**
     * 判断是不是同一天
     * @param calendarBegin
     * @param calendarNow
     * @return
     */
    public static boolean isSameDay(Calendar calendarBegin, Calendar calendarNow) {
        return (calendarBegin.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR)) &&
                (calendarBegin.get(Calendar.MONTH) == calendarNow.get(Calendar.MONTH)) &&
                (calendarBegin.get(Calendar.DATE) == calendarNow.get(Calendar.DATE));
    }

    /**
     * 计算某一时间开始到当天经过多少天
     * @param millis
     * @return
     */
    public static int getNDayFromTimes(long millis) {
        //重置millis当天0点时的时间
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long now = System.currentTimeMillis();
        return (int)((now - cal.getTimeInMillis()) / TimeMillisOneDay);
    }

    /**
     * 获取当前时间的零点时间
     * @param timestamp
     * @return
     */
    public final static int getMorningTime(int timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp * 1000L);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int)(cal.getTimeInMillis() / 1000);
    }

    /**
     * 获取零点时间
     * @return
     */
    public final static int getMorningTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int)(cal.getTimeInMillis() / 1000);
    }

    public final static int timestamp() {
        return (int)(System.currentTimeMillis() / 1000);
    }
}
