package com.game.async.asyncdb;

import com.game.async.asyncdb.orm.BaseDBEntity;

/**
 * @Author: liguorui
 * @Date: 2020/9/19 6:44 下午
 */
public class BaseDBEntitySaver implements ISaver {

    private BaseDBEntity entity;

    public BaseDBEntitySaver(BaseDBEntity entity) {
        this.entity = entity;
    }

    @Override
    public void save() {
        entity.saver();
    }
}
