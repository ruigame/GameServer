package com.game.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: liguorui
 * @Date: 2020/11/30 下午10:21
 */
public class UseTimer {

    private static final Logger LOGGER = LoggerFactory.getLogger("UseTimerLogger");

    private String name;
    private long startTime;
    private long warnUseTime;

    public UseTimer(String name, long warnUseTime) {
        super();
        this.name = name;
        this.warnUseTime = warnUseTime;
        start();
    }

    /**
     * 开始计时
     */
    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public long countUseTime() {
        return System.currentTimeMillis() - startTime;
    }

    public long countUseTimeAndRestart() {
        long useTime = countUseTime();
        start();
        return useTime;
    }

    public long printUseTime() {
        long useTime = countUseTime();
        if (useTime > warnUseTime) {
            LOGGER.warn("{} use {} ms", name, useTime);
        } else {
            LOGGER.warn("{} use {} ms", name, useTime);
        }
        return useTime;
    }

    public void printUseTime(String msg) {
        long useTime = countUseTime();
        if (useTime > warnUseTime) {
            LOGGER.warn("{} use {} ms {}", name, useTime, msg);
        } else {
            LOGGER.warn("{} use {} ms {}", name, useTime, msg);
        }
    }

    public void printUseTimeAndRestart() {
        long useTime = countUseTimeAndRestart();
        if (useTime > warnUseTime) {
            LOGGER.warn("{} use {} ms", name, useTime);
        } else {
            LOGGER.warn("{} use {} ms", name, useTime);
        }
    }
}
