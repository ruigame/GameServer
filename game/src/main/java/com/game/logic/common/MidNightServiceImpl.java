package com.game.logic.common;

import com.game.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2021/2/3 上午12:45
 */
@Component
public class MidNightServiceImpl implements MidNightService {

    @Autowired
    private OnlineService onlineService;

    @Override
    public void onZero() {
        for (PlayerActor playerActor : onlineService.getAllOnlinePlayer()) {
            playerActor.addMessage(this::playerOnZero);
        }
    }

    @Override
    public void playerOnZero(PlayerActor playerActor) {
        if (!TimeUtils.isSameDay(playerActor.getLastZeroTime())) {
            playerActor.getNewCommData().onMidnight();
//            ListenerHandler.getInstance().fireNewMidNight(playerActor);
            playerActor.updateLastZeroTime();
        }
    }
}
