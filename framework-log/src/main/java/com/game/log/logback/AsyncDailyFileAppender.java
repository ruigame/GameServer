package com.game.log.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 异步每日日志Appender
 * @Author: liguorui
 * @Date: 2020/12/2 下午9:18
 */
public class AsyncDailyFileAppender extends DailyFileAppender<ILoggingEvent> {

    private static BlockingQueue<LogTask> blockingQueue;

    private static int maxQueueSize = 20000;

    private static int discardingThreShold = maxQueueSize / 5;

    private static LogWork logWork;

    @Override
    public void start() {
        synchronized (AsyncDailyFileAppender.class) {
            if (logWork == null) {
                blockingQueue = new ArrayBlockingQueue<>(maxQueueSize);
                logWork = new LogWork();
                logWork.setName("AsyncDailyFileAppender");
                logWork.setDaemon(true);
                logWork.start();
            }
        }
        super.start();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isStarted()) {
            return;
        }
        if (isQueueBelowDiscardingThreshold() && isDiscardable(eventObject)) { //小于20%，则丢弃INFO及以下登记日志
            return;
        }
        eventObject.prepareForDeferredProcessing();
        blockingQueue.offer(new LogTask(this, eventObject));//满了不插入
    }

    private boolean isDiscardable(ILoggingEvent event) {
        Level level = event.getLevel();
        return level.toInt() <= Level.INFO_INT;
    }

    private boolean isQueueBelowDiscardingThreshold() {
        return (blockingQueue.remainingCapacity() < discardingThreShold);
    }

    public static void stopWork() {
        synchronized (AsyncDailyFileAppender.class) { //不放在stop实现，因为那个每次重新加载会stop
            if (logWork != null) {
                logWork.interrupt();
                try {
                    logWork.join(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logWork = null;
                blockingQueue = null;
            }
        }
    }

    private static class LogTask {
        private DailyFileAppender<ILoggingEvent> dailyFileAppender;
        private ILoggingEvent event;

        public LogTask(DailyFileAppender<ILoggingEvent> dailyFileAppender, ILoggingEvent event) {
            super();
            this.dailyFileAppender = dailyFileAppender;
            this.event = event;
        }
    }

    private static class LogWork extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    LogTask logTask = blockingQueue.take();
                    logTask.dailyFileAppender.subAppend(logTask.event);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            for (LogTask logTask : blockingQueue) {
                logTask.dailyFileAppender.subAppend(logTask.event);
            }
        }
    }
}
