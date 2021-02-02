package com.game.logic.server.domain;

import com.game.framework.JSONUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @Author: liguorui
 * @Date: 2020/12/16 上午12:01
 */
public class ObjJsonCreater<T> implements ObjCreater<T> {

    private Class<T> clazz;

    public ObjJsonCreater(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public T serialize(String data) {
        if (StringUtils.isBlank(data)) {
            data = "{}";
        }
        return JSONUtils.toObject(data, clazz);
    }

    @Override
    public String deserialize(T obj) {
        return JSONUtils.toJsonStr(obj);
    }
}
