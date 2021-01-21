package com.game.logic.player.dao;

import com.game.async.asyncdb.orm.BaseDao;
import com.game.logic.player.entity.ForbidRoleEntity;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:21
 */
public interface ForbidRoleEntityDao extends BaseDao<ForbidRoleEntity> {

    ForbidRoleEntity get(String account, String server);
}
