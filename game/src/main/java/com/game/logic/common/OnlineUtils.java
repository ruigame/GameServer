package com.game.logic.common;

import com.game.util.Context;

import java.util.Calendar;

/**
 * @Author: liguorui
 * @Date: 2020/12/2 下午9:48
 */
public class OnlineUtils {

    /**
     * 获取当前是开服后的多少填，开服当天是第一天
     * @return
     */
    public final static int getDayAfterOpenServer() {
        return Context.getBean(ConfigService.class).getDayAfterOpenServer();
    }

    /**
     * 指定时间戳的0时0分0秒的时间戳（毫秒）
     */
    public static long getDayStartMillis(long milliTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
