package com.game.logic.cross;

import com.game.util.Listener;

/**
 * @Author: liguorui
 * @Date: 2020/12/14 下午11:30
 */
@Listener
public interface CrossClientInitListener {

    void onClientConnectSuc(String platform, int serverId);
}
