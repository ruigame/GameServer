package com.game.logic.player.domain;

import com.game.base.PlayerActor;

/**
 * @Author: liguorui
 * @Date: 2021/1/19 下午11:11
 */
public enum PlayerStatus {

    UNKNOW,

    /**
     * 主城
     */
    ZHU_CHENG {
        @Override
        public void in(PlayerActor playerActor) {
            super.in(playerActor);
        }

        @Override
        public void out(PlayerActor playerActor) {
            super.out(playerActor);
        }
    },
    /**
     * 挂机
     */
    GUA_JI {
        @Override
        public void in(PlayerActor playerActor) {
            super.in(playerActor);
        }

        @Override
        public void out(PlayerActor playerActor) {
            super.out(playerActor);
        }
    },
    /**
     * 个人BOSS副本
     */
    BOSS_MISSION {
        @Override
        public void in(PlayerActor playerActor) {
            super.in(playerActor);
        }

        @Override
        public void out(PlayerActor playerActor) {
            super.out(playerActor);
        }
    }
    ;

    PlayerStatus() {

    }

    public void in(PlayerActor playerActor) {

    }

    public void out(PlayerActor playerActor) {

    }
}
