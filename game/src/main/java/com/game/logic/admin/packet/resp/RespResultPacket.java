package com.game.logic.admin.packet.resp;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午3:11
 */
@Packet(commandId = PacketId.Admin.RESP_RESULT_PACKET)
public class RespResultPacket extends ResponsePacket {

    private int result;

    @Override
    protected void doResponse(Response response) {
        response.writeInt(result);
    }

    public void result(int result) {
        this.result = result;
    }

    public void success() {
        result = 1;
    }

    public void fail() {
        result = 0;
    }
}
