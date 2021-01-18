package com.game.timer.impl;

import com.game.timer.TimeTriggerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author liguorui
 * @date 2018/1/21 23:28
 */
public abstract class FixedDelayTriggerTask implements TimeTriggerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedDelayTriggerTask.class);

    private String taskName;
    private long executeTime;
    private long delay;

    public FixedDelayTriggerTask(String taskName, long initDelay, long delay, TimeUnit timeUnit) {
        this.taskName = taskName;
        this.executeTime = System.currentTimeMillis() + timeUnit.toMillis(initDelay);
        this.delay = timeUnit.toMillis(delay);
    }

    @Override
    public boolean canTrigger() {
        return true;
    }

    protected abstract void handle(long time);

    @Override
    public void trigger(long time) {
        try {
            handle(time);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        this.executeTime = System.currentTimeMillis() + delay;
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
