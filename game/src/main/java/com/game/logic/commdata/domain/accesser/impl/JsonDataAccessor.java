package com.game.logic.commdata.domain.accesser.impl;

import com.alibaba.fastjson.JSONObject;
import com.game.logic.commdata.domain.accesser.DataWrapper;
import com.game.logic.commdata.domain.accesser.IJsonDataAccessor;
import com.game.logic.commdata.entity.NewCommData;

/**
 * @author liguorui
 * @date 2018/2/5 20:18
 */
public class JsonDataAccessor extends DataWrapper implements IJsonDataAccessor {

    private JSONObject json;

    public JsonDataAccessor(NewCommData data) {
        super(data);
    }

    @Override
    public JSONObject getJson() {
        if (json == null) {
            json = JSONObject.parseObject(data.getValue());
        }
        return json;
    }

    @Override
    public void saveJson() {
        if (json != null) {
            data.setValue(json.toJSONString());
        }
    }

    @Override
    public void reset() {
        json = null;
    }
}
