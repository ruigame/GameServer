package com.game.logic.player.listener;

import com.game.logic.common.PlayerActor;
import com.game.util.Listener;

/**
 * @Author: liguorui
 * @Date: 2021/1/20 下午10:59
 */
@Listener
public interface PlayerLogoutListener {

    void onLogout(PlayerActor playerActor);
}
