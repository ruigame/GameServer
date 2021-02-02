package com.game.async.asyncdb;

import com.game.async.asyncdb.anotation.Persistent;
import com.game.util.ICloseEvent;
import com.game.util.SimpleThreadFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liguorui
 * @date 2018/1/15 01:44
 */
@Component
public class AsyncDBService implements ApplicationContextAware, ICloseEvent {

    /**
     * 队列数量
     */
    private int threads = 16;

    private ExceptionCallback callback;

    @Autowired
    private ISyncStrategy syncStrategy;

    private SyncQueuePool syncQueuePool;

    private final Map<Class<?>, Synchronizer> synchronizerMap = new ConcurrentHashMap<Class<?>, Synchronizer>();

    @Override
    public void onServerClose() {
        stop();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, Synchronizer> map = context.getBeansOfType(Synchronizer.class);
        for (Synchronizer synchronizer : map.values()) {
            Class<?>[] interfaces = synchronizer.getClass().getInterfaces();
            synchronizerMap.put(interfaces[0], synchronizer);
        }
    }

    @PostConstruct
    public void init() {
        syncQueuePool = new SyncQueuePool(new SimpleThreadFactory("AsyncDB"), threads, callback, syncStrategy);
    }

    public void delete(AsynDBEntity entity) {
        entity.serialize();
        this.doOperation(entity, Operation.DELETE);
    }

    public void update(AsynDBEntity entity) {
        entity.serialize();
        try {
            this.doOperation(entity, Operation.UPDATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert(AsynDBEntity entity) {
        entity.serialize();
        try {
            this.doOperation(entity, Operation.INSERT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean doOperation(AsynDBEntity entity, Operation operation) {
        if (entity.submit(operation)) {
            if (entity.getSynchronizer() == null) {
                entity.setSynchronizer(this.synchronizerMap.get(getPersistentClass(entity.getClass())));
            }
            return this.syncQueuePool.submit(entity);
        }
        return true;
    }

    private Class<?> getPersistentClass(Class<?> entityClass) {
        Persistent persistent = entityClass.getAnnotation(Persistent.class);
        return persistent.syncClass();
    }

    public boolean stop() {
        try {
            return this.syncQueuePool.shutdown(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

}
