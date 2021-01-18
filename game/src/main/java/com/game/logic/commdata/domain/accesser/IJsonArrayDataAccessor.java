package com.game.logic.commdata.domain.accesser;

import com.alibaba.fastjson.JSONArray;

/**
 * @author liguorui
 * @date 2018/2/5 19:18
 */
public interface IJsonArrayDataAccessor extends IDataReset {

    JSONArray getJsonArray();
    void saveJsonArray();
}
