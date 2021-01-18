package com.game.async.asyncdb.orm;

import com.game.async.asyncdb.Synchronizer;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 6:05 下午
 */
public interface BaseDao<E> extends Synchronizer<E> {

    E get(Serializable id);

    List<E> getAll();
}
