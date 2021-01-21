package com.game.logic.player.dao;

import com.game.async.asyncdb.orm.BaseDao;
import com.game.logic.player.entity.ForBidIpEntity;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:14
 */
public interface ForbidIpEntityDao extends BaseDao<ForBidIpEntity> {

    ForBidIpEntity get(String ip, int serverId);

    int updateDataByServerId(int serverId, int value);
}
