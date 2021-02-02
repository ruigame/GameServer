package com.game.timer.impl;

import com.game.timer.AbstractTriggerTaskExecutor;
import com.game.timer.RefreshDelayQueue;
import com.game.timer.TimeTriggerTask;
import com.game.timer.TriggerFuture;
import com.game.util.CoreThreadFactory;
import com.game.util.ExceptionUtils;
import com.game.util.RunTimeUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 触发任务执行器
 * @author liguorui
 * @date 2018/1/14 18:29
 */
@Component
public class TriggerTaskExecutor extends AbstractTriggerTaskExecutor {

    private ExecutorService service;
    private RefreshDelayQueue<DelayedTriggerFutureTask> taskDelayQueue = new RefreshDelayQueue<DelayedTriggerFutureTask>();

    public TriggerTaskExecutor() {
        this(RunTimeUtils.TWICE_CPU, "TriggerTaskExecutor");
    }

    public TriggerTaskExecutor(int threadNum, String threadPrefix) {
        service = Executors.newCachedThreadPool(new CoreThreadFactory(threadPrefix));
        for (int i = 0; i < threadNum; i++) {
            service.execute(new Runnable() {

                @Override
                public void run() {
                    while (!service.isShutdown()) {
                        try {
                            DelayedTriggerFutureTask trigger = taskDelayQueue.take();
                            LOGGER.debug("start task {}", trigger);
                            trigger.triggerTask();
                            LOGGER.debug("finish task {}", trigger);
                            if (!trigger.isCanceled() && !trigger.isFinish()) {
                                taskDelayQueue.add(trigger);
                            }
                        } catch (InterruptedException e1) {
                        } catch (Exception e) {
                            ExceptionUtils.log(e);
                        }
                    }
                }
            });
        }
    }

    public TriggerFuture addTask(TimeTriggerTask task) {
        try {
            if (!task.canTrigger()) {
                LOGGER.debug("{} 任务已失效", task);
                return TriggerFuture.FINISH_FUTURE;
            }
            if (contains(task)) {
                ExceptionUtils.log("{} task exit", task);
                return TriggerFuture.FINISH_FUTURE;
            }
            DelayedTriggerFutureTask futureTask = new DelayedTriggerFutureTask(task);
            taskDelayQueue.add(futureTask);
            return futureTask;
        } catch (Exception e) {
            ExceptionUtils.log(e);
        }
        return TriggerFuture.FINISH_FUTURE;
    }

    public List<TriggerFuture> addAllTask(Collection<? extends TimeTriggerTask> taskCollection) {
        List<TriggerFuture> futureList = new ArrayList<TriggerFuture>(taskCollection.size());
        for (TimeTriggerTask task : taskCollection) {
            futureList.add(addTask(task));
        }
        return futureList;
    }

    public void refresh() {
        taskDelayQueue.refresh();
    }

    public void shutdown(long timeout, TimeUnit unit) {
        service.shutdownNow();
        try {
            service.awaitTermination(timeout, unit);
        } catch (Exception e) {
            ExceptionUtils.log(e);
        }
    }

    public boolean isShutdown() {
        return service.isShutdown();
    }

    public void removeTask(TimeTriggerTask task) {
        taskDelayQueue.remove(new DelayedTriggerFutureTask(task));
    }

    public void clear() {
        taskDelayQueue.clear();
    }

    public boolean contains(TimeTriggerTask task) {
        return taskDelayQueue.contains(new DelayedTriggerFutureTask(task));
    }

    private class DelayedTriggerFutureTask extends AbstractDelayedTriggerFutureTask {

        private volatile boolean canceled;

        public DelayedTriggerFutureTask(TimeTriggerTask task) {
            super(task);
        }

        @Override
        public void cancel() {
            canceled = true;
            taskDelayQueue.remove(this);
        }

        public boolean isCanceled() {
            return canceled;
        }
    }
}
