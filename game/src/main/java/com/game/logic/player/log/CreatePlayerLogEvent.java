package com.game.logic.player.log;

import com.game.logic.common.ConfigService;
import com.game.logic.common.PlayerActor;
import com.game.logic.common.PlayerLogEvent;
import com.game.util.Context;

/**
 * @Author: liguorui
 * @Date: 2021/1/25 下午10:27
 */
public class CreatePlayerLogEvent extends PlayerLogEvent {

    public CreatePlayerLogEvent(PlayerActor playerActor, String pid, String gid, String param) {
        super(Context.getBean(ConfigService.class).getPlatform(), Context.getBean(ConfigService.class).getOriServerId(),
                playerActor.getAccount(), playerActor.getId(), playerActor.getName(), pid, gid, param);
    }

    @Override
    public String message() {
        return messagePlayerActor().toString();
    }
}
