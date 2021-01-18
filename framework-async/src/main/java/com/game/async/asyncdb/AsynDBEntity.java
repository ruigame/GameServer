package com.game.async.asyncdb;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author liguorui
 * @date 2018/1/15 00:44
 */
@SuppressWarnings("rawtypes")
public class AsynDBEntity {

    private static final AtomicReferenceFieldUpdater<AsynDBEntity, AsynDBState> stateUpdater =
            AtomicReferenceFieldUpdater.newUpdater(AsynDBEntity.class, AsynDBState.class, "state");

    /**
     * 实体状态
     */
    private transient volatile AsynDBState state = AsynDBState.NOMAL;

    private transient Synchronizer synchronizer;

    private transient SyncQueue suncQueue;

    public AsynDBEntity() {
        this.state = AsynDBState.NOMAL;
    }

    /**
     * 是否需要提交
     * @param operation
     * @return
     */
    public boolean submit(Operation operation) {
        AsynDBState currentState = null;
        for (;;) {
            currentState = state;
            //判断操作是否能在当前状态操作
            if (operation.isCanOperationAt(currentState)) {
                //查看该操作是否要覆盖之前的状态
                if (operation.isNeedToChangeAt(currentState)) {
                    //更改状态失败返回继续尝试
                    if (!stateUpdater.compareAndSet(this, currentState, operation.STATE)) {
                        continue;
                    }
                }
                return currentState == AsynDBState.NOMAL;
            }
            throw new RuntimeException("[" + this + "] submit exception" + currentState + " " + operation);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean trySync(int maxTime) {
        int trySyncCount = 0;
        for (;;) {
            AsynDBState currentState = state;
            if (stateUpdater.compareAndSet(this, currentState, currentState != AsynDBState.DELETE ? AsynDBState.NOMAL : AsynDBState.DELETED)) {
                while (trySyncCount ++ < maxTime) {
                    if (currentState.doOperation(synchronizer, this)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    public Synchronizer getSynchronizer() {
        return this.synchronizer;
    }

    public void setSynchronizer(Synchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    public void serialize() {

    }

    public int getHash() {
        return hashCode();
    }

    public AsynDBState getAsynDBState() {
        return this.state;
    }

    public SyncQueue getSuncQueue() {
        return suncQueue;
    }

    public void setSuncQueue(SyncQueue suncQueue) {
        this.suncQueue = suncQueue;

    }
}
