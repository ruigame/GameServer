package com.game.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 并发HashSet
 * @Author: liguorui
 * @Date: 2020/9/19 12:47 下午
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> {

    private ConcurrentHashMap<E, Boolean> map = new ConcurrentHashMap<>();

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean add(E e) {
        return map.putIfAbsent(e, Boolean.TRUE) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) != null;
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        map.clear();
    }
}
