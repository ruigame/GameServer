package com.game.logic.player.domain;

import com.game.logic.common.PlayerActor;
import com.game.logic.player.entity.PlayerEntity;
import com.google.common.base.Preconditions;

/**
 * 玩家经验类
 * @Author: liguorui
 * @Date: 2021/1/18 下午11:14
 */
public class PlayerExp {

    private PlayerActor playerActor;

    public PlayerExp(PlayerActor playerActor) {
        this.playerActor = playerActor;
    }

    public static PlayerExp create(PlayerActor playerActor) {
        return new PlayerExp(playerActor);
    }

    /**
     * 玩家当前经验
     * @return
     */
    public long getExp() {
        return getPlayerEntity().getExp();
    }

    public PlayerEntity getPlayerEntity() {
        return playerActor.getPlayerEntity();
    }

    public long addExp(long value, OperationType opType) {
        if (value == 0) {
            return value;
        }
        Preconditions.checkArgument(value > 0, "Exp value negative! %s", value);
        if (!this.isMaxLevel()) {

        }
    }

    public void deductLevel(long alterExp, OperationType opType) {
        int oldLevel = playerActor.getLevel();
        int newLevel = oldLevel - 1;
        getPlayerEntity().alterLevel(-1);
        afterLevelChanged(oldLevel, newLevel, opType);
        long maxExp = getMaxExp();
        setExp(0);
        long curNewExp = Math.max(0, maxExp - alterExp);
        alterExp(curNewExp, opType, true);
    }

    /**
     * 增加等级
     * @param alter
     * @param opType
     * @return
     */
    public int addLevel(int alter, OperationType opType) {
        int oldLevel = playerActor.getLevel();
        int newLevel = oldLevel + alter;
        int maxLevel = 100;
        if (newLevel > maxLevel) {
            alter = maxLevel - oldLevel;
            newLevel = maxLevel;
        }
        getPlayerEntity().alterLevel(alter);
        afterLevelChanged(oldLevel, newLevel, opType);
        return alter;
    }

    /**
     * 修改玩家经验
     * @param alter
     * @param opType
     */
    protected void alterExp0(long alter, OperationType opType) {
        Preconditions.checkArgument(alter > 0, "Exp value negative! %s", alter);
        long gainExp = alter;
        long exp = getExp(); //当前经验
        int oldLevel = playerActor.getLevel();
        while (alter > 0 && !isMaxLevel()) {
            long eachAlter = getMaxExp() - exp; //还差多少经验升级
            if (eachAlter < 0) {
                //跑这里一般是因为改了各个等级升级所需的经验导致，直接就升一级吧
                eachAlter = 0;
                exp = getMaxExp();
            }
            eachAlter = eachAlter < alter ? eachAlter : alter; //如果大于等于获得的经验就等于获得的经验
            Preconditions.checkArgument(eachAlter >= 0, "ALter : %s eachAlter:%s, playerId:%s, getMaxExp:%s, exp:%s",
                          alter, eachAlter, playerActor.getId(), getMaxExp(), exp);

            alter -= eachAlter;
            exp += eachAlter;
            if (exp == getMaxpExp())
        }
    }

    public void setExp(long exp) {
        getPlayerEntity().changeExp(exp);
    }
}
