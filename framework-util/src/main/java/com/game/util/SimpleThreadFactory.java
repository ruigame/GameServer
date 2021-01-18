package com.game.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单的线程工厂
 * @author liguorui
 * @date 2017/7/1 21:56
 */
public class SimpleThreadFactory implements ThreadFactory{

    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private ThreadGroup group;
    private String groupName;

    public SimpleThreadFactory() {
        init();
        this.groupName = group.getName();
    }

    public SimpleThreadFactory(String groupName) {
        init();
        this.groupName = groupName;
    }

    private void init() {
        Executors.defaultThreadFactory();
        SecurityManager securityManager = System.getSecurityManager();//安全管理器
        group = securityManager == null ? Thread.currentThread().getThreadGroup()
                            : securityManager.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String threadName = groupName + "-thread-" + threadNumber.getAndIncrement();
        Thread t =new Thread(group,runnable,threadName);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return  t;
    }

}
