package com.game.logic.player.packet.resp;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;

/**
 * @Author: liguorui
 * @Date: 2021/1/25 下午10:34
 */
@Packet(commandId = PacketId.Base.RESP_RECONNECT)
public class RespReconnectPacket extends ResponsePacket {

    //状态 1：正常 2：重新登陆 3：刷新页面重连
    private static final int success = 1;
    private static final int relogin = 2;
    private static final int refresh = 3;

    private int status;

    @Override
    protected void doResponse(Response response) {
        response.writeByte(status);
    }

    public void success() {
        this.status = success;
    }

    public void relogin() {
        this.status = relogin;
    }

    public void refresh() {
        this.status = refresh;
    }
}
