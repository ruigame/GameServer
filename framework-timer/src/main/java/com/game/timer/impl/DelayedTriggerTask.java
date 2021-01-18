package com.game.timer.impl;

import com.game.timer.TimeTriggerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author liguorui
 * @date 2018/1/21 23:33
 */
public class DelayedTriggerTask implements TimeTriggerTask, Delayed {

    private static final TimeUnit TIME_UNIT = TimeUnit.MICROSECONDS; //时间精度

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayedTriggerTask.class);

    private TimeTriggerTask task;

    public DelayedTriggerTask(TimeTriggerTask task) {
        super();
        this.task = task;
    }

    @Override
    public boolean canTrigger() {
        return task.canTrigger();
    }

    @Override
    public void trigger(long time) {
        try {
            task.trigger(time);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public long getTriggerTime() {
        return task.getTriggerTime();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long delayMill = Math.max(getTriggerTime() - System.currentTimeMillis(), 0L);
        long delay = TIME_UNIT.convert(delayMill, TimeUnit.MILLISECONDS);
        if (TIME_UNIT.toMillis(delay) != delayMill) {
            delay ++;
        }
        return unit.convert(delay, TIME_UNIT);
    }

    @Override
    public int compareTo(Delayed other) {
        if (this == other) {
            return 0;
        }
        long delay = getDelay(TimeUnit.MILLISECONDS);
        long otherDelay = other.getDelay(TimeUnit.MILLISECONDS);
        return delay > otherDelay ? 1 : (delay == otherDelay ? 0 : -1);
    }

    @Override
    public String toString() {
        return task.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((task == null) ? 0 : task.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DelayedTriggerTask other = (DelayedTriggerTask) obj;
        if (task == null) {
            if (other.task != null)
                return false;
        } else if (!task.equals(other.task))
            return false;
        return true;
    }
}
