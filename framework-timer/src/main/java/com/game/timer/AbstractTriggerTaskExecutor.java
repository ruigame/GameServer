package com.game.timer;

import com.game.timer.impl.DelayedTriggerTask;
import com.game.timer.impl.FixedDelayTriggerTask;
import com.game.timer.impl.FixedRateTriggerTask;
import com.game.timer.impl.ImmediateTriggerTask;
import com.game.util.ExceptionUtils;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author liguorui
 * @date 2018/1/14 18:30
 */
public abstract class AbstractTriggerTaskExecutor implements ITriggerTaskExecutor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractTriggerTaskExecutor.class);

    private Map<String, TriggerFuture> taskId2FutureMap = new ConcurrentHashMap<>();

    public abstract TriggerFuture addTask(TimeTriggerTask task);

    @Override
    public TriggerFuture execute(String taskName, final Runnable command) {
        return addTask(new ImmediateTriggerTask(taskName) {
            @Override
            protected void handle(long time) {
                command.run();
            }
        });
    }

    @Override
    public TriggerFuture schedule(final String taskName, final Runnable command, long delay,
                                                          TimeUnit unit) {
        long executeTime = System.currentTimeMillis() + unit.toMillis(delay);
        return schedule(taskName, command, executeTime);
    }

    @Override
    public TriggerFuture schedule(final String taskName, final Runnable command, final long executeTime) {
        return addTask(new TimeTriggerTask() {

            private boolean trigger = false;

            @Override
            public void trigger(long time) {
                try {
                    command.run();
                } catch (Exception e) {
                    ExceptionUtils.log(e);
                }
                trigger = true;
            }

            @Override
            public long getTriggerTime() {
                return executeTime;
            }

            @Override
            public boolean canTrigger() {
                return !trigger;
            }

            @Override
            public String toString() {
                return taskName;
            }
        });
    }

    @Override
    public TriggerFuture scheduleAtFixedRate(String taskName, final Runnable Command,
                                                                     long initialDelay, long period, TimeUnit unit) {
        return addTask(new FixedRateTriggerTask(taskName, initialDelay, period, unit) {
            @Override
            protected void handle(long time) {
                Command.run();
            }
        });
    }

    @Override
    public TriggerFuture scheduleWithFixedDelay(String taskName, final Runnable command,
                                                                        long initialDelay, long delay, TimeUnit unit) {
        return addTask(new FixedDelayTriggerTask(taskName, initialDelay, delay, unit) {
            @Override
            protected void handle(long time) {
                command.run();
            }
        });
    }

    public abstract void removeTask(TimeTriggerTask task);

    @Override
    public TriggerFuture scheduleUniqueTask(final String taskId, final Runnable command, long delay,
                                                                    TimeUnit unit) {
        final long executeTime = System.currentTimeMillis() + unit.toMillis(delay);
        return scheduleUniqueTask(taskId, command, executeTime);
    }

    public TriggerFuture scheduleUniqueTask(final String taskId, final Runnable command,
                                                                    final long executeTime) {
        Preconditions.checkNotNull(taskId, "taskId is not null");
        TriggerFuture oldTriggerFuture = taskId2FutureMap.remove(taskId);
        if (oldTriggerFuture != null) {
            oldTriggerFuture.cancel();
        }

        TriggerFuture triggerFuture = addTask(new TimeTriggerTask() {

            private boolean trigger = false;

            @Override
            public void trigger(long time) {
                try {
                    command.run();
                } catch (Exception e) {
                    ExceptionUtils.log(e);
                }
                taskId2FutureMap.remove(taskId);
                trigger = true;
            }

            @Override
            public long getTriggerTime() {
                return executeTime;
            }

            @Override
            public boolean canTrigger() {
                return !trigger;
            }

            @Override
            public String toString() {
                return taskId;
            }
        });
        putTriggerFuture(taskId, triggerFuture);
        return triggerFuture;
    }

    private void putTriggerFuture(String taskId, TriggerFuture triggerFuture) {
        TriggerFuture oldTriggerFuture = taskId2FutureMap.put(taskId, triggerFuture);
        if (oldTriggerFuture != null) {
            oldTriggerFuture.cancel();
        }
    }

    @Override
    public void cancelUniqueTask(String taskId) {
        TriggerFuture oldTriggerFuture = taskId2FutureMap.remove(taskId);
        if (oldTriggerFuture != null) {
            oldTriggerFuture.cancel();
        }
    }

    protected abstract class AbstractDelayedTriggerFutureTask implements Delayed, TriggerFuture {

        protected TimeTriggerTask task;
        protected Delayed delayed;

        public AbstractDelayedTriggerFutureTask(TimeTriggerTask task) {
            this.task = task;
            this.delayed = new DelayedTriggerTask(task);
        }

        @Override
        public int compareTo(Delayed o ) {
            return delayed.compareTo(o);
        }

        @Override
        public boolean isFinish() {
            return !this.task.canTrigger();
        }

        @Override
        public abstract void cancel();

        public void triggerTask() {
            task.trigger(task.getTriggerTime());
            if (isFinish()) {
                afterFinish();
            }
        }

        protected void afterFinish() {

        }

        @Override
        public long getDelay(TimeUnit unit) {
            return delayed.getDelay(unit);
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
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            AbstractDelayedTriggerFutureTask other = (AbstractDelayedTriggerFutureTask)obj;
            if (task == null) {
                if (other.task != null) {
                    return false;
                }
            } else if (!task.equals(other.task)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return task.toString();
        }
    }
}
