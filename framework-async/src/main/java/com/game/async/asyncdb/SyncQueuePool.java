package com.game.async.asyncdb;

import com.game.util.CoreThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author liguorui
 * @date 2018/1/21 20:52
 */
public class SyncQueuePool {

    private final static Logger logger = LoggerFactory.getLogger(SyncQueuePool.class);

    /**
     * 队列池
     */
    private SyncQueue[] pool;

    /**
     * 队列池容量
     */
    private int poolSize;

    /**
     * 监控线程
     */
    private ScheduledExecutorService monitor;

    private final ExecutorService workerExecutors;

    private volatile boolean stop;

    public SyncQueuePool(ThreadFactory threadFactory, int poolSize, ExceptionCallback callback, ISyncStrategy syncStrategy) {
        super();
        this.workerExecutors = Executors.newFixedThreadPool(poolSize, threadFactory);
        this.pool = new SyncQueue[poolSize];
        this.poolSize = poolSize;
        for (int i = 0; i < poolSize; i++) {
            pool[i] = new SyncQueue(i, callback, syncStrategy);
            workerExecutors.execute(pool[i]);
        }

        monitor = Executors.newScheduledThreadPool(1, new CoreThreadFactory("SyncDBMonitor"));
        monitor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                int totalWaiting = 0;
                for (SyncQueue each : pool) {
                    SyncStats stats = each.stats();
                    logger.info("Queue: {}, SyncCount:{}, WaitingSize:{}, PeriodNum:{}", each.getQueueId(),
                            stats.getTotal(), stats.getWaiting(), stats.getPeriodNum());
                }
                if (totalWaiting > 500) {
                    logger.warn("TotalWaiting: {}", totalWaiting);
                }
            }
        }, 0, 60L, TimeUnit.SECONDS);
    }

    public boolean submit(AsynDBEntity synchronizable) {
        if (this.stop) {
            return false;
        }

        SyncQueue sq = synchronizable.getSuncQueue();
        if (sq == null) {
            int hash = synchronizable.getHash() % this.poolSize;
            hash = Math.abs(hash);
            sq = pool[hash];
            synchronizable.setSuncQueue(sq);
        }

        return sq.submit(synchronizable);
    }

    public boolean shutdown(long millis) throws InterruptedException {
        stop = true;
        for (int i = 0; i < poolSize; i++) {
            pool[i].shutdown(millis);
        }

        workerExecutors.shutdown();
        workerExecutors.awaitTermination(millis, TimeUnit.MILLISECONDS);
        monitor.shutdown();;
        return true;
    }
}
