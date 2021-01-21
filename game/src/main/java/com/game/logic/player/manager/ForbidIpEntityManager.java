package com.game.logic.player.manager;

import com.game.logic.player.dao.ForbidIpEntityDao;
import com.game.logic.player.entity.ForBidIpEntity;
import com.game.util.ServerStarter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:41
 */
@Component
public class ForbidIpEntityManager implements ServerStarter {

    @Autowired
    private ForbidIpEntityDao forbidIpEntityDao;

    /**
     * ip-ForBidIpEntity
     */
    private Cache<String, ForBidIpEntity> ip2ForbidCache = CacheBuilder.newBuilder().build();

    public ForBidIpEntity getForBidIpEntityByIp(String ip, int serverId) {
        ForBidIpEntity entity = ip2ForbidCache.getIfPresent(ip);
        if (entity == null) {
            ForBidIpEntity temp = new ForBidIpEntity(ip, serverId);
            entity = ConcurrentUtils.putIfAbsent(this.ip2ForbidCache.asMap(), ip, temp);
            if (entity == temp) {
                entity.insert();
            }
        }
        return entity;
    }

    public ForBidIpEntity get(String ip) {
        ForBidIpEntity entity = ip2ForbidCache.getIfPresent(ip);
        return entity;
    }

    public void deleteForBidIpEntity(String ip) {
        ForBidIpEntity entity = get(ip);
        if (entity != null) {
            ip2ForbidCache.invalidate(ip);
            entity.delete();
        }
    }

    public void updateDataByServerId(int serverId, int value) {
        forbidIpEntityDao.updateDataByServerId(serverId, value);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void init() {
        for (ForBidIpEntity forBidIpEntity : forbidIpEntityDao.getAll()) {
            ip2ForbidCache.put(forBidIpEntity.getIp(), forBidIpEntity);
        }
    }
}
