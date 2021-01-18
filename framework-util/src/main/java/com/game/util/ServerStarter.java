package com.game.util;

/**
 * @Author: liguorui
 * @Date: 2020/9/19 12:42 下午
 */
public interface ServerStarter {

    /**
     * 优先级最低
     */
    int HIGHEST = Integer.MIN_VALUE;

    int COMMON = 0;
    int LOWEST = Integer.MAX_VALUE;

    /**
     * 数字越小越先执行
     * @return
     */
    default int getOrder() {return COMMON;};

    void init();
}
