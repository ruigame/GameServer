package com.game.logic.player.dao;

import com.game.async.asyncdb.orm.GameDaoSupport;
import com.game.logic.player.domain.forbid.ForbidEntityKey;
import com.game.logic.player.entity.ForbidRoleEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:15
 */
@Component
@Transactional
public class ForbidRoleEntityDaoImpl extends GameDaoSupport<ForbidRoleEntity> implements ForbidRoleEntityDao{


    @Override
    public ForbidRoleEntity get(String account, String server) {
        return get(new ForbidEntityKey(account, server));
    }
}
