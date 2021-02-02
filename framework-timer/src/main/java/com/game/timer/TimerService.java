package com.game.timer;

import com.game.util.SimpleThreadFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * 定时器服务，提供一个集中管理的系统定时任务执行者
 * 该服务将分散的系统定时器集中管理
 * 建议尽量使用TimerService，而非直接创建缓存池
 * @Author: liguorui
 * @Date: 2021/2/3 上午12:06
 */
@Component
public class TimerService {

    private final ScheduledExecutorService scheduler;

    public TimerService() {
        scheduler = Executors.newScheduledThreadPool(5, new SimpleThreadFactory("timer-service"));
    }

    /**
     * 获取服务器定时器
     * @return
     */
    public final ScheduledExecutorService getSchedulerExecutor() {
        return scheduler;
    }

    /**
     * 执行一个延迟毫秒级别的任务
     * @param command
     * @param delay
     * @return
     */
    public final ScheduledFuture<?> scheduleMilliSeconds(Runnable command, long delay) {
        return getSchedulerExecutor().schedule(command, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 执行一个延迟秒级别的任务
     * @param command
     * @param delay
     * @return
     */
    public final ScheduledFuture<?> scheduleSeconds(Runnable command, int delay) {
        return getSchedulerExecutor().schedule(command, delay, TimeUnit.SECONDS);
    }

    /**
     * 执行一个自定义的延迟任务
     * @param command
     * @param delay
     * @return
     */
    public final ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return getSchedulerExecutor().schedule(command, delay, unit);
    }

    /**
     * 创建并执行一个在给定初始化延迟后首次启用的定期操作，后续操作具有定期的周期
     * @param command
     * @param delay
     * @return
     */
    public final ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long delay) {
        return getSchedulerExecutor().scheduleAtFixedRate(command, delay, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 创建并执行一个在给定初始化延迟后首次启用的定期操作，后续操作具有定期的周期
     * @param command
     * @param delay
     * @return
     */
    public final ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long delay) {
        return getSchedulerExecutor().scheduleAtFixedRate(command, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 运行完成和下次开始固定间隔
     * @param command
     * @param delay
     * @return
     */
    public final ScheduledFuture<?> scheduleWithFixedRate(Runnable command, long delay) {
        return getSchedulerExecutor().scheduleWithFixedDelay(command, delay, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 运行完成和下次开始固定间隔
     * @param command
     * @param delay
     * @return
     */
    public final ScheduledFuture<?> scheduleWithFixedRate(Runnable command, long initialDelay, long delay) {
        return getSchedulerExecutor().scheduleWithFixedDelay(command, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }

    public Future<?> submit(Runnable task) {
        return getSchedulerExecutor().submit(task);
    }
}
