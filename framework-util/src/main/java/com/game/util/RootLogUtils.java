package com.game.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: liguorui
 * @Date: 2020/12/2 下午8:54
 */
public class RootLogUtils {

    private static final Logger log = LoggerFactory.getLogger(RootLogUtils.class);

    public static void warnLog(Throwable e) {
        log.warn("", e);
    }

    public static void warnLog(String msg) {
        log.warn(msg);
    }

    public static void warnLog(String format, Object... args) {
        log.warn(format, args);
    }

    public static void infoLog(String msg) {
        log.info(msg);
    }

    public static void infoLog(String format, Object... args) {
        log.info(format, args);
    }
}
