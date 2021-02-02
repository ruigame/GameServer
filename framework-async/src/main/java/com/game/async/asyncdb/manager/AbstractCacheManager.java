package com.game.async.asyncdb.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * @Author: liguorui
 * @Date: 2020/9/19 6:56 下午
 */
public abstract class AbstractCacheManager<K, V> {

    public Cache<K, V> cache;

    public AbstractCacheManager() {
        this(CacheBuilder.newBuilder().weakValues().build());
    }

    public AbstractCacheManager(Cache<K, V> cache) {
        this.cache = cache;
    }

    /**
     * 从数据库中查找数据
     * @param key
     * @return
     */
    public abstract V getFromDB(K key);

    private V loadFromDB(K key) {
        V value = getFromDB(key);
        if (value != null) {
            onAfterLoad(value);
        }
        return value;
    }

    protected void onAfterLoad(V value) {}

    protected abstract V create(K key, Object... args);

    /**
     * 把数据插入数据库
     * @param v
     */
    protected void save(V v){

    }

    /**
     * 添加进缓存
     * @param k
     * @param v
     * @return
     */
    protected V addCache(K k, V v) {
        return ConcurrentUtils.putIfAbsent(cache.asMap(), k, v);
    }

    /**
     * 从缓存中获取，如果缓存中没有，则从数据库中查找
     * 如果数据库中没有，则新建并放入缓存
     * @param key
     * @param args
     * @return
     */
    public V getItemOrCreate(K key, Object... args) {
        try {
            return cache.get(key, new Callable<V>() {
                @Override
                public V call() throws Exception {
                    V v = loadFromDB(key);
                    if (v == null) {
                        v = create(key);
                        save(v);
                    }
                    return v;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从缓存中获取，如果缓存中没有，则从数据库中查找，如果数据库都没有则返回null
     * @param key
     * @return
     */
    public V getItem(K key) {
        try {
            V v = cache.getIfPresent(key);
            if (v == null) {
                v = loadFromDB(key);
                if (v != null) {
                    v = addCache(key, v);
                }
            }
            return v;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从缓存中获取，如果缓存中没有则返回null
     * @param key
     * @return
     */
    public V getItemByCache(K key) {
        try {
            V v = cache.getIfPresent(key);
            return v;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Collection<V> getAllCache() {
        return cache.asMap().values();
    }

    public void invalidate(K key) {
        cache.invalidate(key);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }
}
