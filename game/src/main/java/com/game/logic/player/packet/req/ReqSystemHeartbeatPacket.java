package com.game.logic.player.packet.req;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Request;
import com.game.net.packet.RequestBeforeLoginPacket;

/**
 * 请求系统心跳
 * @Author: liguorui
 * @Date: 2021/1/20 下午11:36
 */
@Packet(commandId = PacketId.Base.REQ_SYSTEM_HEARTBEAT)
public class ReqSystemHeartbeatPacket extends RequestBeforeLoginPacket {
    @Override
    public void read(Request request) throws Exception {

    }
}

