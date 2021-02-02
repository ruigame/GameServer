package com.game.logic.commdata.domain.accesser.impl;

import com.alibaba.fastjson.JSON;
import com.game.logic.commdata.PlayerCommObj;
import com.game.logic.commdata.domain.accesser.DataWrapper;
import com.game.logic.commdata.domain.accesser.IObjectDataAccessor;
import com.game.logic.commdata.entity.NewCommData;
import com.game.framework.TypeReference;

/**
 * @author liguorui
 * @date 2018/2/5 20:23
 */
public class JsonParseAccessor extends DataWrapper implements IObjectDataAccessor {

    private Object obj;

    public JsonParseAccessor(NewCommData data) {
        super(data);
    }

    public <T> T getObject(Class<T> clz) {
        if (obj == null) {
            T obj = JSON.parseObject(data.getValue(), clz);
            if (obj instanceof PlayerCommObj) {
                ((PlayerCommObj)obj).setAccessor(this);
            }
            this.obj = obj;
        }
        return (T)obj;
    }

    public void saveObject() {
        if (obj != null) {
            synchronized (obj) {
                data.setValue(JSON.toJSONString(obj));
            }
        }
    }

    @Override
    public void reset() {
        obj = null;
    }

    @Override
    public <T> T getObject(String json, TypeReference<T> reference) {
        if (obj == null) {
            T obj = JSON.parseObject(json, reference.getType());
            if (obj instanceof PlayerCommObj) {
                ((PlayerCommObj)obj).setAccessor(this);
            }
            this.obj = obj;
        }
        return (T)obj;
    }

    @Override
    public <T extends PlayerCommObj> T getPlayerCommObj(Class<T> clazz) {
        return getObject(clazz);
    }
}
