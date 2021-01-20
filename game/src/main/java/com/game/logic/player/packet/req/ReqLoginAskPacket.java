package com.game.logic.player.packet.req;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Request;
import com.game.net.packet.RequestBeforeLoginPacket;

/**
 * 请求登陆，发送消息
 * @Author: liguorui
 * @Date: 2021/1/20 下午11:29
 */
@Packet(commandId = PacketId.Base.REQ_LOGIN_ASK)
public class ReqLoginAskPacket extends RequestBeforeLoginPacket {
    @Override
    public void read(Request request) throws Exception {

    }
}
