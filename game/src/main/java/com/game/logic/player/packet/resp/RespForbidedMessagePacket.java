package com.game.logic.player.packet.resp;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午9:54
 */
@Packet(commandId = PacketId.Base.RESP_FORBIDED_TIME_LESS)
public class RespForbidedMessagePacket extends ResponsePacket {

    private int day;

    private int hour;

    private int minute;

    private String reason;

    public void init(int[]time, String reason) {
        this.day = time[0];
        this.hour = time[1];
        this.minute = time[2];
        this.reason = reason;
    }

    @Override
    protected void doResponse(Response response) {
        response.writeInt(day);
        response.writeByte(hour);
        response.writeByte(minute);
        response.writeString(reason);
    }
}
