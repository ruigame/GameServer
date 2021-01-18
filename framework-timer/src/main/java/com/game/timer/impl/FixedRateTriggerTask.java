package com.game.timer.impl;

import com.game.timer.TimeTriggerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 固定周期
 * @author liguorui
 * @date 2018/1/7 19:11
 */
public abstract class FixedRateTriggerTask implements TimeTriggerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedRateTriggerTask.class);

    private String taskName;
    private long executeTime;
    private long period;

    public FixedRateTriggerTask(String taskName, long initDelay, long period, TimeUnit timeUnit) {
        this.taskName = taskName;
        this.executeTime = System.currentTimeMillis() + timeUnit.toMillis(initDelay);
        this.period = timeUnit.toMillis(period);
    }

    protected abstract void handle(long time);

    @Override
    public boolean canTrigger() {
        return true;
    }

    @Override
    public void trigger(long time) {
        try {
            handle(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.executeTime += period;
    }

    @Override
    public long getTriggerTime() {
        return executeTime;
    }

    @Override
    public String toString() {
        return taskName;
    }
}
