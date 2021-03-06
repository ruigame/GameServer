package com.game.logic.player.packet.resp;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;

/**
 * @Author: liguorui
 * @Date: 2021/1/25 下午10:01
 */
@Packet(commandId = PacketId.Base.RESP_NAME_RANDOM)
public class RespNameRandomPacket extends ResponsePacket {

    private String name;

    public void init(String name) {
        this.name = name;
    }

    @Override
    protected void doResponse(Response response) {
        response.writeString(name);
    }
}
