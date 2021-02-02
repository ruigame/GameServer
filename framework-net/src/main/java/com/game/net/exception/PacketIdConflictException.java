package com.game.net.exception;

import com.game.net.packet.AbstractPacket;

/**
 * @Author: liguorui
 * @Date: 2021/2/2 下午8:53
 */
public class PacketIdConflictException extends GameException{

    private static final long serialVersionUID = 1958654173666018182L;

    public PacketIdConflictException(int packetId, Class<? extends AbstractPacket> packet1, Class<? extends AbstractPacket> packet2) {
        super(String.format("Conflict packet id %s found : %s and %s", packet1, packet1.getName(), packet2.getName()));
    }
}
