package com.game.util;

import java.util.Calendar;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 8:32 下午
 */
public class TimeUtils {

    public static final long TimeMillisOneHour = 24 * 60 * 60 * 1000;

    public static final long TimeMillisOneDay = 24 * 60 * 60 * 1000;

    public final static int curTimestamp() {
        return (int)(System.currentTimeMillis() / 1000);
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
