package com.game.util;

/**
 * @Author: liguorui
 * @Date: 2020/11/24 下午4:48
 */
public class RunTimeUtils {

    /**
     * 两倍cpu核数
     */
    public static final int TWICE_CPU = Runtime.getRuntime().availableProcessors() * 2;

    public  static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
