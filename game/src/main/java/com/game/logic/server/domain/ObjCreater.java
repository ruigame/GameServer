package com.game.logic.server.domain;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 下午11:27
 */
public interface ObjCreater<T> {

    T serialize(String data);

    String deserialize(T obj);
}
