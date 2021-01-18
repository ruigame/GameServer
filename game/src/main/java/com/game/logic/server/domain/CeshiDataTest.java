package com.game.logic.server.domain;

import com.game.logic.server.manager.ServerInfoManager;
import com.game.util.Context;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 下午11:55
 */
public class CeshiDataTest {

    public void ceshi() {
        CeShiData ceShiData = Context.getBean(ServerInfoManager.class).getServerInfoObject(ConfigKey.CESHI_DATA, CeShiData.class);
    }
}
