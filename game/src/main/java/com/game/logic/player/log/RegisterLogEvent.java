package com.game.logic.player.log;

import com.game.logic.common.PlayerLogEvent;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午9:16
 */
public class RegisterLogEvent extends PlayerLogEvent {

    public RegisterLogEvent(String platform, int serverId, String account, long playerId, String playerName, String pid, String gid, String param) {
        super(platform, serverId, account, playerId, playerName, pid, gid, param);
    }

    @Override
    public String message() {
        return messagePlayerActor().toString();
    }
}
