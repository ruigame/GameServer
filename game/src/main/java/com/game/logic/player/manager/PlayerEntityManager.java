package com.game.logic.player.manager;

import com.game.async.asyncdb.Synchronizer;
import com.game.util.GameSession;
import com.game.logic.player.dao.PlayerEntityDao;
import com.game.logic.player.domain.Gender;
import com.game.logic.player.domain.Role;
import com.game.logic.player.domain.RoleType;
import com.game.logic.player.entity.PlayerEntity;
import com.game.util.ExceptionUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 8:11 下午
 */
@Component
public class PlayerEntityManager implements Synchronizer<PlayerEntity> {

    @Autowired
    private PlayerEntityDao playerEntityDao;

    private Cache<Long, PlayerEntity> playerId2Entity = CacheBuilder.newBuilder().softValues().build();

    public PlayerEntity getPlayerEntityById(long playerId) {
        PlayerEntity entity = playerId2Entity.getIfPresent(playerId);
        if (entity == null) {
            entity = playerEntityDao.get(playerId);
            if (entity != null) {
                entity = ConcurrentUtils.putIfAbsent(this.playerId2Entity.asMap(), playerId, entity);
            }
        }
        return entity;
    }

    public PlayerEntity createPlayerEntity(GameSession session, int serverId, String account,
                                            long playerId, String name, RoleType roleType, Gender gender, boolean persistent) {
        int x = 0;
        int y = 0;
        int sceneId = 0;
        int roleIndex = 1;
        int initLevel = 1;
        PlayerEntity entity = new PlayerEntity(serverId, playerId, account, name, session.getParam(), sceneId, x, y, initLevel, session.getIp());
        PlayerEntity oldEntity = this.playerId2Entity.asMap().putIfAbsent(entity.getPlayerId(), entity);
        if (oldEntity != null) {
            ExceptionUtils.log("playerId {} 重复， 创建{}角色失败", entity.getPlayerId(), account);
            return null;
        }
        Role newRole = new Role(roleIndex, roleType, gender);
        entity.addRole(newRole);
        entity.showRole(roleIndex);
        if (persistent) {
            entity.insert();
        }
        return entity;
    }

    public Map<String, Long> getAllName() {
        return this.playerEntityDao.getAllName();
    }

    public List<Object[]> getAllAccount() {
        return this.playerEntityDao.getAllAccount();
    }

    public List<PlayerEntity> getDuplicateName() {
        List<PlayerEntity> playerList = new ArrayList<>();
        List<PlayerEntity> tempList = this.playerEntityDao.getDuplicateName();
        for (PlayerEntity temp : tempList) {
            PlayerEntity entity = ConcurrentUtils.putIfAbsent(this.playerId2Entity.asMap(), temp.getPlayerId(), temp);
            playerList.add(entity);
        }
        return playerList;
    }

    @Override
    public boolean insert(PlayerEntity object) {
        this.playerEntityDao.insert(object);
        return true;
    }

    @Override
    public boolean update(PlayerEntity object) {
        this.playerEntityDao.update(object);
        return true;
    }

    @Override
    public boolean delete(PlayerEntity object) {
        return false;
    }
}
