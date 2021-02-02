package com.game.logic.player.log;

import com.game.logic.common.PlayerActor;
import com.game.logic.common.PlayerLogEvent;
import com.game.logic.player.domain.OperationType;

/**
 * @Author: liguorui
 * @Date: 2021/1/29 上午12:31
 */
public class PlayerLevelChangeLogEvent extends PlayerLogEvent {

    private int oldLevel;
    private int newLevel;
    private OperationType operationType;

    public PlayerLevelChangeLogEvent(PlayerActor playerActor, int oldLevel, OperationType operationType) {
        super(playerActor);
        this.oldLevel = oldLevel;
        this.newLevel = playerActor.getLevel();
        this.operationType = operationType;
    }

    @Override
    public String message() {
        return messagePlayerActor()
                .appendKeyValue("oldLevel", oldLevel)
                .appendKeyValue("newLevel", newLevel)
                .appendKeyValue("operationType", operationType.getId())
                .toString();
    }
}
