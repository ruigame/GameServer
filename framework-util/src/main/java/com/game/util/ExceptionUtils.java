package com.game.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 6:29 下午
 */
public class ExceptionUtils {

    private static final Logger log = LoggerFactory.getLogger(ExceptionUtils.class);

    public static void log(Throwable e) {
        log.error("", e);
    }

    public static void log(Throwable e, String msg) {
        log.error(msg, e);
    }

    public static void log(Throwable e, String msg, Object... args) {
        log.error(String.format(msg, args), e);
    }

    public static void log(String msg) {
        log.error(msg);
    }

    public static void log(String format, Object... args) {
        log.error(format, args);
    }

    /**
     * 把当前错误堆栈打印出来
     * @param msg
     */
    public static void logStack(String msg) {
        log.error("", new RuntimeException(msg));
    }
}
