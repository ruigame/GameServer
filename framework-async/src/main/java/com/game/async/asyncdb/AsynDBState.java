package com.game.async.asyncdb;

/**
 * @author liguorui
 * @date 2018/1/15 00:49
 */
public enum AsynDBState {

    /**
     * 正常状态
     */
    NOMAL() {
        @Override
        public boolean doOperation(Synchronizer<AsynDBEntity> synchronizer, AsynDBEntity asynDBEntity) {
            return true;
        }
    },

    DELETED() {
        @Override
        public boolean doOperation(Synchronizer<AsynDBEntity> synchronizer, AsynDBEntity asynDBEntity) {
            return true;
        }
    },

    /**
     * 删除状态 属于提交持久化状态
     */
    DELETE() {
        @Override
        public boolean doOperation(Synchronizer<AsynDBEntity> synchronizer, AsynDBEntity asynDBEntity) {
            try {
                return synchronizer.delete(asynDBEntity);
            } catch (Exception e) {
                throw new SyncException(String.format("%s", asynDBEntity.getClass().getSimpleName()), e);
            }
        }
    },

    /**
     * 更新状态 属于提交持久化状态
     */
    UPDATE() {
        @Override
        public boolean doOperation(Synchronizer<AsynDBEntity> synchronizer, AsynDBEntity asynDBEntity) {
            try {
                return synchronizer.update(asynDBEntity);
            } catch (Exception e) {
                throw new SyncException(String.format("%s", asynDBEntity.getClass().getSimpleName()), e);
            }
        }
    },

    /**
     * 插入状态 属于提交持久化状态
     */
    INSERT() {
        @Override
        public boolean doOperation(Synchronizer<AsynDBEntity> synchronizer, AsynDBEntity asynDBEntity) {
            try {
                return synchronizer.insert(asynDBEntity);
            } catch (Exception e) {
                throw new SyncException(String.format("%s", asynDBEntity.getClass().getSimpleName()), e);
            }
        }
    },
    ;

    /**
     * 持久化对象
     * @param synchronizer 持久器
     * @param asynDBEntity 持久化成功返回true
     * @return
     */
    public abstract boolean doOperation(Synchronizer<AsynDBEntity> synchronizer, AsynDBEntity asynDBEntity);
}
