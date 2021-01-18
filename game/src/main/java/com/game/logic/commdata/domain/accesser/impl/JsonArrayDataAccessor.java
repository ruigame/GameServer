package com.game.logic.commdata.domain.accesser.impl;

import com.alibaba.fastjson.JSONArray;
import com.game.logic.commdata.domain.accesser.DataWrapper;
import com.game.logic.commdata.domain.accesser.IJsonArrayDataAccessor;
import com.game.logic.commdata.entity.NewCommData;

/**
 * @author liguorui
 * @date 2018/2/5 20:15
 */
public class JsonArrayDataAccessor extends DataWrapper implements IJsonArrayDataAccessor {

    private JSONArray jsonArray;

    public JsonArrayDataAccessor(NewCommData data) {
        super(data);
    }

    @Override
    public JSONArray getJsonArray() {
        if (jsonArray == null) {
            jsonArray = JSONArray.parseArray(data.getValue());
        }
        return jsonArray;
    }

    @Override
    public void saveJsonArray() {
        if (jsonArray != null) {
            data.setValue(jsonArray.toJSONString());
        }
    }

    @Override
    public void reset() {
        jsonArray = null;
    }
}
