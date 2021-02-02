package com.game.framework;

import com.game.net.packet.AbstractPacket;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午5:01
 */
public interface PacketSender {

    void sendPacket(AbstractPacket packet);

    long getPlayerId();
}
