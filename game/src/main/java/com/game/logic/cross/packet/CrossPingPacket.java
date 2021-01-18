package com.game.logic.cross.packet;

import com.game.PacketId;
import com.game.net.packet.CrossPacket;
import com.game.net.packet.Packet;
import com.game.net.packet.Request;
import com.game.net.packet.Response;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 上午12:10
 */
@Packet(commandId = PacketId.Cross.CROSS_PING)
public class CrossPingPacket extends CrossPacket {
    @Override
    protected void doWriteCross(Response response) {

    }

    @Override
    public void read(Request request) throws Exception {

    }
}
