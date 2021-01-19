package com.game.logic.player.domain;

import com.game.logic.player.entity.PlayerEntity;

/**
 * @Author: liguorui
 * @Date: 2021/1/19 下午11:47
 */
public class PlayerNameCompare {

    private PlayerEntity entity;

    public PlayerNameCompare(PlayerEntity entity) {
        super();
        this.entity = entity;
    }

    public boolean isNameMore(PlayerNameCompare other) {
        if (entity.getLevel() >= other.getEntity().getLevel()) {
            return true;
        }
        if (entity.getLevel() < other.getEntity().getLevel()) {
            return false;
        }
        if (entity.getPlayerId() < other.getEntity().getPlayerId()) {
            return true;
        }
        return false;
    }

    public PlayerEntity getEntity() {
        return entity;
    }

    public void setEntity(PlayerEntity entity) {
        this.entity = entity;
    }
}
