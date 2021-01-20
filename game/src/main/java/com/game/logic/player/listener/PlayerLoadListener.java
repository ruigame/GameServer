package com.game.logic.player.listener;

import com.game.logic.common.PlayerActor;
import com.game.util.Listener;

/**
 * @Author: liguorui
 * @Date: 2021/1/20 下午10:55
 */
@Listener
public interface PlayerLoadListener {

    //数字越小越先执行
    int HIGHEST = Integer.MIN_VALUE;
    int COMMON = 0;
    int LOWEST = Integer.MAX_VALUE;

    void onLoad(PlayerActor playerActor);

    default int getLoadOrder() {
        return COMMON;
    }
}
