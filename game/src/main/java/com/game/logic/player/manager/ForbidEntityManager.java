package com.game.logic.player.manager;

import com.game.logic.player.dao.ForbidRoleEntityDao;
import com.game.logic.player.entity.ForbidRoleEntity;
import com.game.util.ServerStarter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:46
 */
@Component
public class ForbidEntityManager implements ServerStarter {

    @Autowired
    private ForbidRoleEntityDao forbidRoleEntityDao;

    private Cache<String, ForbidRoleEntity> ip2ForbidCache = CacheBuilder.newBuilder().build();

    public ForbidRoleEntity getForbidRoleEntityBykey(String account, String serverId) {
        String key = account + "_" + serverId;
        ForbidRoleEntity entity = ip2ForbidCache.getIfPresent(key);
        if (entity == null) {
            ForbidRoleEntity temp = new ForbidRoleEntity(account, serverId);
            entity = ConcurrentUtils.putIfAbsent(this.ip2ForbidCache.asMap(), key, temp);
            if (entity == temp) {
                entity.insert();
            }
        }
        return entity;
    }

    public ForbidRoleEntity getNotInsert(String account, String serverId) {
        String key = account + "_" + serverId;
        ForbidRoleEntity entity = ip2ForbidCache.getIfPresent(key);
        return entity;
    }

    public void deleteForbidRoleEntity(ForbidRoleEntity forbid) {
        ForbidRoleEntity entity = forbid;
        if (entity != null) {
            String key = forbid.getAccount() + "_" + forbid.getServer();
            ip2ForbidCache.invalidate(key);
            entity.delete();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void init() {
        for (ForbidRoleEntity forbid : forbidRoleEntityDao.getAll()) {
            String key = forbid.getAccount() + "_" + forbid.getServer();
            ip2ForbidCache.put(key, forbid);
        }
    }
}
