package com.game.async.asyncdb;

/**
 * @author liguorui
 * @date 2018/1/15 00:38
 */
public interface Synchronizer<T> {

    boolean insert(T object);

    boolean update(T object);

    boolean delete(T object);
}
