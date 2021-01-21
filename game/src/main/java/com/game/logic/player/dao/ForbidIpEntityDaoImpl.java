package com.game.logic.player.dao;

import com.game.async.asyncdb.orm.GameDaoSupport;
import com.game.logic.player.domain.forbid.ForbidIpDataKey;
import com.game.logic.player.entity.ForBidIpEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:17
 */
@Component
@Transactional
public class ForbidIpEntityDaoImpl extends GameDaoSupport<ForBidIpEntity> implements ForbidIpEntityDao {


    @Override
    public ForBidIpEntity get(String ip, int serverId) {
        return get(new ForbidIpDataKey(ip, serverId));
    }

    @Override
    public int updateDataByServerId(int serverId, int value) {
        return getSession().createQuery("update ForBidIpEntity forBidIpEntity set forBidIpEntity.serverId=:serverId" +
                " where forBidIpEntity.serverId=:value").setParameter("serverId", serverId).setParameter("value", value).executeUpdate();
    }
}
