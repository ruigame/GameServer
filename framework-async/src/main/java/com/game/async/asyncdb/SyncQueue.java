package com.game.async.asyncdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * @author liguorui
 * @date 2018/1/21 21:08
 */
public class SyncQueue implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(SyncQueue.class);

    private static AsynDBEntity SHUTDOWN_ENTITY = new AsynDBEntity();

    /**
     * 队列id
     */
    private final int queueId;

    /**
     * 是否停止
     */
    private volatile boolean stop = false;

    /**
     * 已经同步到数据库的对象次数
     */
    private volatile long syncCount = 0;

    /**
     * 待同步队列
     */
    private final BlockingQueue<AsynDBEntity> syncQueue = new LinkedTransferQueue<AsynDBEntity>();

    /**
     * 异常回调
     */
    private ExceptionCallback callback;

    /**
     * 同步策略
     */
    private ISyncStrategy syncStrategy;

    /**
     * 上次统计时的总数量
     */
    private volatile long preNum;

    public SyncQueue(int queueId, ExceptionCallback callback, ISyncStrategy syncStrategy) {
        this.callback = callback;
        this.queueId = queueId;
        this.syncStrategy = syncStrategy;
    }

    public boolean submit(AsynDBEntity synchronizable) {
        if (this.stop) {
            return false;
        }
        return this.syncQueue.add(synchronizable);
    }

    public boolean shutdown(long millis) throws InterruptedException {
        if (this.stop) {
            return false;
        }
        this.stop = true;
        this.syncQueue.add(SHUTDOWN_ENTITY); //发送停止实体激活队列
        return true;
    }

    public int getWaitingSize() {
        return syncQueue.size();
    }

    public long getSyncCount() {
        return syncCount;
    }

    public int getQueueId() {
        return queueId;
    }

    public SyncStats stats() {
        long total = this.getSyncCount();
        long periodNum = total - this.preNum;
        this.preNum = total;
        int waiting = getWaitingSize();
        return new SyncStats(waiting, total, periodNum);
    }

    @Override
    public void run() {
        while (true) {
            final int numEachLoop = this.syncStrategy.getNumEachLoop();
            final int tryTime = this.syncStrategy.getTryTime();
            for (int i = 0; i < numEachLoop; i++) {
                AsynDBEntity entity = null;
                try {
                    entity = syncQueue.take();
                } catch (InterruptedException e) {
                    logger.error("SyncQueueInterrupterException {}", queueId, e);
                }

                if (entity == null || entity == SHUTDOWN_ENTITY) {
                    break;
                }

                try {
                    entity.trySync(tryTime);
                    syncCount ++;
                } catch (Exception e) {
                    try {
                        callback.onException(e);
                    } catch (Exception e1) {
                        logger.error("CallbackException", e1);
                    }
                }
            }

            if (this.stop) {
                if (!syncQueue.isEmpty()) {
                    //要求停服，但同步队列非空，则不休眠，快速提交所有同步对象
                    continue;
                }
                break;
            } else {
                //没有停服，通过休眠来控制同步速率
                try {
                    int waitingSize = this.syncQueue.size();
                    long sleeptime = this.syncStrategy.getSleepTime(waitingSize);
                    Thread.sleep(sleeptime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
