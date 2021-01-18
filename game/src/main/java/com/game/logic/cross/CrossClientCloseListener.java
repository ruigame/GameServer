package com.game.logic.cross;

import com.game.net.cross.CrossClient;
import com.game.util.Listener;

/**
 * 跨服客户端关闭
 * @Author: liguorui
 * @Date: 2020/12/14 下午11:34
 */
@Listener
public interface CrossClientCloseListener {

    void onCrossClientClose(CrossClient crossClient);
}
