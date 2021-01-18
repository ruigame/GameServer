package com.game.logic.commdata;

import com.alibaba.fastjson.annotation.JSONField;
import com.game.logic.commdata.domain.accesser.impl.JsonParseAccessor;

/**
 * @Author: liguorui
 * @Date: 2020/12/8 下午9:38
 */
public abstract class PlayerCommObj {

    private JsonParseAccessor accessor;

    @JSONField(serialize = false)
    public JsonParseAccessor getAccessor() {
        return accessor;
    }

    public void setAccessor(JsonParseAccessor accessor) {
        this.accessor = accessor;
    }

    public void update() {
        accessor.saveObject();
    }
}
