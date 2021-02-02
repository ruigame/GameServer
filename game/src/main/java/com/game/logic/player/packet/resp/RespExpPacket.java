package com.game.logic.player.packet.resp;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;

/**
 * @Author: liguorui
 * @Date: 2021/1/29 上午12:14
 */
@Packet(commandId = PacketId.Role.RESP_EXP)
public class RespExpPacket extends ResponsePacket {

    private long exp;
    private long diff;

    public void init(long exp, long diff) {
        this.exp = exp;
        this.diff = diff;
    }

    @Override
    protected void doResponse(Response response) {
        response.writeLong(exp);
        if (diff < 0) {
            diff = 0;
        }
        response.writeLong(diff);
    }
}
