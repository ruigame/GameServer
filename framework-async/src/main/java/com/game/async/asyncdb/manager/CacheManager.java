package com.game.async.asyncdb.manager;

import com.game.async.asyncdb.ISaver;
import com.game.async.asyncdb.BaseDBEntity;
import com.google.common.cache.Cache;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @Author: liguorui
 * @Date: 2020/9/19 7:10 下午
 */
public abstract class CacheManager<K, V extends BaseDBEntity> extends AbstractCacheManager<K, V> {

    public CacheManager() {
        super();
    }

    public CacheManager(Cache<K, V> cache) {
        super(cache);
    }

    /**
     * 新建一个实体，放到缓存，并且保存
     * @param key
     * @param args
     * @return
     */
    public V createAndCache(K key, Object... args) {
        try {
            return cache.get(key, new Callable<V>() {
                @Override
                public V call() throws Exception { //防止其他线程拿到缓存时候还没有insert状态
                    V v = create(key, args);
                    ISaver saver = v.saver();
                    if (saver != null) {
                        saver.save();
                    }
                    return v;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    protected void onAfterLoad(V value) {
        value.deserialize();
    }

    @Override
    protected void save(V v) {
        ISaver saver = v.saver();
        if (Objects.nonNull(saver)) {
            saver.save();
        }
    }
}
