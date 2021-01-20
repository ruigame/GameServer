package com.game.logic.player.packet.req;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Request;
import com.game.net.packet.RequestBeforeLoginPacket;

/**
 * 请求随机名(不分性别，仅供展示)
 * @Author: liguorui
 * @Date: 2021/1/20 下午11:38
 */
@Packet(commandId = PacketId.Base.REQ_NAME_RANDOM_SHOW)
public class ReqRandomNameShowPacket extends RequestBeforeLoginPacket {
    @Override
    public void read(Request request) throws Exception {

    }
}
