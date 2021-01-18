package com.game.logic.player.dao;

import com.game.async.asyncdb.orm.BaseDao;
import com.game.logic.player.entity.PlayerEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 5:59 下午
 */
public interface PlayerEntityDao extends BaseDao<PlayerEntity> {

    Long getMaxId();

    Map<String, Long> getAllName();

    List<Object[]> getAllAccount();

    List<Integer> getAllServerIds();

    Long getMaxIdByServerId(Integer serverId);

    Integer selectPayNum();

    /**
     * 查询重名玩家
     * @return
     */
    List<PlayerEntity> getDuplicateName();

    /**
     * 计算总充值数
     * @return
     */
    Long countTotalPay();

    /**
     * 当前最大等级
     * @return
     */
    Integer getMaxLevel();
}
