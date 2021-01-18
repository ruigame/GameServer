package com.game.logic.commdata.domain.accesser;

import com.alibaba.fastjson.JSONObject;

/**
 * @author liguorui
 * @date 2018/2/5 19:19
 */
public interface IJsonDataAccessor extends IDataReset {

    JSONObject getJson();
    void saveJson();
}
