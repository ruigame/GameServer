package com.game.logic.player.packet.req;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Request;
import com.game.net.packet.RequestBeforeLoginPacket;

/**
 * 请求随机名
 * @Author: liguorui
 * @Date: 2021/1/20 下午11:15
 */
@Packet(commandId = PacketId.Base.REQ_NAME_RANDOM)
public class ReqNameRandomPacket extends RequestBeforeLoginPacket {

    private byte gender;

    @Override
    public void read(Request request) throws Exception {
        this.gender = request.readByte();
    }

    public byte getGender() {
        return gender;
    }
}
