package com.game.timer;

import java.util.concurrent.TimeUnit;

/**
 * @author liguorui
 * @date 2018/1/14 18:19
 */
public interface ITriggerTaskExecutor {

    public abstract TriggerFuture execute(String taskName, Runnable command);

    public abstract TriggerFuture schedule(String taskName, Runnable command, long delay, TimeUnit unit);

    /**
     *
     * @param taskName
     * @param command
     * @param executeTime 执行时间戳 （单位毫秒，时间小于当前时间立即执行）
     * @return
     */
    public abstract TriggerFuture schedule(String taskName, Runnable command, long executeTime);

    public abstract TriggerFuture scheduleAtFixedRate(String taskName, Runnable command,
                                                                              long initialDelay, long period, TimeUnit unit);

    public abstract TriggerFuture scheduleWithFixedDelay(String taskName, Runnable command, long initialDelay,
                                                                                 long delay, TimeUnit unit);

    /**
     * 调度唯一任务，当有两个任务ID相同则替换
     * @param taskId
     * @param command
     * @param delay
     * @param unit
     * @return
     */
    public abstract TriggerFuture scheduleUniqueTask(String taskId, Runnable command, long delay, TimeUnit unit);

    public abstract TriggerFuture scheduleUniqueTask(String taskId, Runnable command, long executeTime);

    /**
     * 根据任务id取消唯一任务
     * @param taskId
     */
    abstract void cancelUniqueTask(String taskId);

}
