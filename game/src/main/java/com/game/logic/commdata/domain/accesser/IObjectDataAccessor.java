package com.game.logic.commdata.domain.accesser;

import com.game.logic.commdata.PlayerCommObj;
import com.game.base.TypeReference;

/**
 * @author liguorui
 * @date 2018/2/5 19:22
 */
public interface IObjectDataAccessor extends IDataReset {

    /**
     * 使用PlayerActor.getPlayerCommObj替代
     * @param <T>
     * @return
     */
//    <T> T getObject(Class<T> clz);

    <T extends PlayerCommObj> T getPlayerCommObj(Class<T> clazz);

    <T> T getObject(String json, TypeReference<T> typeReference);

//    void saveObject();
}
