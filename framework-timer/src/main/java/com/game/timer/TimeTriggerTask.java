package com.game.timer;

/**
 * @author liguorui
 * @date 2018/1/7 18:58
 */
public interface TimeTriggerTask {

    boolean canTrigger();

    void trigger(long time);

    long getTriggerTime();
}
