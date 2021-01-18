package com.game.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liguorui
 * @date 2018/1/14 19:44
 */
public class CoreThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber;
    private final String namePrefix;
    private final boolean daemon;

    public CoreThreadFactory(String namePrefix) {
        this(namePrefix, false);
    }

    public CoreThreadFactory(String namePrefix, boolean daemon) {
        threadNumber = new AtomicInteger(1);
        SecurityManager localSecurityManager = System.getSecurityManager();
        group = localSecurityManager == null ? Thread.currentThread()
                .getThreadGroup() : localSecurityManager.getThreadGroup();
        this.namePrefix = (new StringBuilder(String.valueOf(namePrefix)))
                .append("-").append("-thread-").toString();
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable paramRunnable) {
        Thread localThread = new Thread(group, paramRunnable,
                (new StringBuilder(String.valueOf(namePrefix))).append(
                        threadNumber.getAndIncrement()).toString(), 0L);
        if (localThread.isDaemon()) {
            localThread.setDaemon(daemon);
        }
        if (localThread.getPriority() != 5) {
            localThread.setPriority(5);
        }
        return localThread;
    }
}
