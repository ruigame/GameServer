package com.game.logic.server.manager;

import com.game.async.asyncdb.Synchronizer;
import com.game.logic.server.dao.ServerInfoDao;
import com.game.logic.server.domain.ConfigKey;
import com.game.logic.server.domain.ObjCreater;
import com.game.logic.server.domain.ObjJsonCreater;
import com.game.logic.server.domain.ServerInfoObj;
import com.game.logic.server.entity.ServerInfo;
import com.game.util.ExceptionUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 下午10:23
 */
@Component
public class ServerInfoManager implements Synchronizer<ServerInfo> {

    @Autowired
    private ServerInfoDao configDao;

    private Cache<ConfigKey, ServerInfo> configMap = CacheBuilder.newBuilder().build();

    private Cache<ConfigKey, Object> objectCache = CacheBuilder.newBuilder().build();

    public void init() {
        List<ServerInfo> list = configDao.getAll();
        if (list != null) {
            for (ServerInfo config : list) {
                ConfigKey configKey = config.getKey();
                if (configKey == null) {
                    ExceptionUtils.log("{} 不存在枚举", config.getCkey());
                    continue;
                }
                configMap.put(configKey, config);
            }
        }
    }

    public ServerInfo getConfig(ConfigKey configKey) {
        ServerInfo config = configMap.getIfPresent(configKey);
        if (config == null) {
            config = new ServerInfo();
            config.setCkey(configKey.toString());
            config.setCvalue(configKey.getDefaultValue());
            ServerInfo oldServerInfo = configMap.asMap().putIfAbsent(configKey, config);
            if (oldServerInfo == null) {
                configDao.insert(config);
            } else {
                config = oldServerInfo;
            }
        }
        return config;
    }

    public void reset(ConfigKey configKey) {
        ServerInfo config = getConfig(configKey);
        config.setCvalue(configKey.getDefaultValue());
        config.update();

        objectCache.invalidate(configKey);
    }

    @Override
    public boolean update(ServerInfo config) {
        configDao.update(config);
        return true;
    }

    @Override
    public boolean insert(ServerInfo object) {
        configDao.insert(object);
        return true;
    }

    @Override
    public boolean delete(ServerInfo object) {
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends ServerInfoObj> T getServerInfoObject(ConfigKey configKey, ObjCreater<T> objCreater) {
        try {
            return (T) objectCache.get(configKey, new Callable<T>() {
                @Override
                public T call() throws Exception {
                    ServerInfo serverInfo = getConfig(configKey);
                    String data = serverInfo.getCvalue();
                    T obj = objCreater.serialize(data);
                    obj.setConfigKey(configKey);
                    obj.setObjCreater(objCreater);
                    return obj;
                }
            });
        } catch (Exception e) {
            ExceptionUtils.log(e);
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends ServerInfoObj> T getServerInfoObject(ConfigKey configKey, Class<T> clazz) {
        T obj = (T)objectCache.getIfPresent(configKey);
        if (obj != null) {
            return obj;
        }
        return (T)getServerInfoObject(configKey, new ObjJsonCreater<>(clazz));
    }
}
