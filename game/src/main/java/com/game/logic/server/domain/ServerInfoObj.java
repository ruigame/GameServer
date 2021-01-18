package com.game.logic.server.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.game.logic.server.entity.ServerInfo;
import com.game.logic.server.manager.ServerInfoManager;
import com.game.util.Context;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 下午11:26
 */
public abstract class ServerInfoObj {

    private ConfigKey configKey;

    private ObjCreater objCreater;

    @JSONField(serialize = false)
    public ConfigKey getConfigKey() {
        return configKey;
    }

    public void setConfigKey(ConfigKey configKey) {
        this.configKey = configKey;
    }

    public ObjCreater getObjCreater() {
        return objCreater;
    }

    public void setObjCreater(ObjCreater objCreater) {
        this.objCreater = objCreater;
    }

    public void update() {
        ServerInfo serverInfo = Context.getBean(ServerInfoManager.class).getConfig(configKey);
        synchronized (serverInfo) {
            serverInfo.setCvalue(objCreater.deserialize(this));
        }
        serverInfo.update();
    }
}
