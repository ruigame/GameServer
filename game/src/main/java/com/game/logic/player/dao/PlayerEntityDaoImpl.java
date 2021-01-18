package com.game.logic.player.dao;

import com.game.async.asyncdb.orm.GameDaoSupport;
import com.game.logic.player.entity.PlayerEntity;
import com.google.common.collect.Maps;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 6:38 下午
 */
@Component
@Transactional
public class PlayerEntityDaoImpl extends GameDaoSupport<PlayerEntity> implements PlayerEntityDao {


    @Override
    public Long getMaxId() {
        Session session = getSession();
        Long maxId = session.createQuery("SELECT playerId FROM Player ORDER BY playerId DESC", Long.class).getSingleResult();
        return maxId;
    }

    @Override
    public Map<String, Long> getAllName() {
        Session session = getSession();
        List<Object[]> list = session.createNamedQuery("getAllName", Object[].class).list();
        Map<String, Long> map = Maps.newHashMapWithExpectedSize(list.size());
        for (Object[] each : list) {
            map.put((String)each[0], (long)each[1]);
        }
        return map;
    }

    @Override
    public List<Object[]> getAllAccount() {
        Session session = getSession();
        List<Object[]> list = session.createNamedQuery("getAllAccount", Object[].class).list();
        return list;
    }

    @Override
    public List<Integer> getAllServerIds() {
        Session session = getSession();
        return session.createNamedQuery("getAllServerIds", Integer.class).list();
    }

    @Override
    public Long getMaxIdByServerId(Integer serverId) {
        Session session = getSession();
        Query<Long> query = session.createNamedQuery("getMaxIdByServerId", Long.class);
        query.setParameter("serverId", serverId);
        return query.getSingleResult();
    }

    @Override
    public Integer selectPayNum() {
        Session session = getSession();
        Query<Number> query = session.createNamedQuery("selectPayNum", Number.class);
        return query.getSingleResult().intValue();
    }

    @Override
    public List<PlayerEntity> getDuplicateName() {
        Session session = getSession();
        Query<PlayerEntity> query = session.createNamedQuery("getDuplicateName", PlayerEntity.class);
        List<PlayerEntity> playerList = query.list();
        for (PlayerEntity playerEntity : playerList) {
            playerEntity.deserialize();
        }
        return playerList;
    }

    @Override
    public Long countTotalPay() {
        Session session = getSession();
        Query<Long> query = session.createNamedQuery("countTotalPay", Long.class);
        return query.getSingleResult();
    }

    @Override
    public Integer getMaxLevel() {
        Session session = getSession();
        Query<Integer> query = session.createNamedQuery("getMaxLevel", Integer.class);
        return query.getSingleResult();
    }
}
