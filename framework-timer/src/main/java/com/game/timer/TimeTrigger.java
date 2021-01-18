package com.game.timer;

/**
 * @Author: liguorui
 * @Date: 2020/11/24 下午5:47
 */
public interface TimeTrigger {

    void setStartTime(long startTime);

    /**
     * 是否能够触发
     * @return
     */
    boolean canTrigger();

    boolean trigger();

    /**
     * 下次触发时间，当不能触发是为-1
     * @return
     */
    long getTriggerTime();

    boolean trigger(long time);

    long getLastFireTime();

    /**
     * 剩余触发时间（毫秒），当下次触发时间小于当前时间返回0
     * @return
     */
    long getLeftTime();
}
