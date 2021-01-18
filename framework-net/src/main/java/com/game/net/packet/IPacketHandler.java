package com.game.net.packet;

import com.game.util.GameSession;

/**
 * @Author: liguorui
 * @Date: 2020/12/1 下午10:38
 */
public interface IPacketHandler {

    PacketType getPacketType();

    void handlePacket(GameSession session, AbstractPacket packet, PacketHandlerWrapper wrapper);
}
