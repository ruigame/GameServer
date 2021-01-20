package com.game.logic.sys;

import com.game.logic.common.PlayerActor;
import com.game.util.GameSession;
import com.game.util.Listener;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 下午10:20
 */
@Listener
public interface PlayerCreateListener {

    void onCreate(PlayerActor playerActor, GameSession gameSession);
}
