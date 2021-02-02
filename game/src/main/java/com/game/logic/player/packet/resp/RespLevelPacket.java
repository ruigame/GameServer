package com.game.logic.player.packet.resp;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;
import com.game.util.TimeUtils;

/**
 * @Author: liguorui
 * @Date: 2021/1/29 上午12:25
 */
@Packet(commandId = PacketId.Role.RESP_LEVEL)
public class RespLevelPacket extends ResponsePacket {

    private int level;
    private long maxExp;

    public void init(int level, long maxExp) {
        this.level = level;
        this.maxExp = maxExp;
    }

    @Override
    protected void doResponse(Response response) {
        response.writeShort(level);
        response.writeLong(maxExp);
        response.writeInt(TimeUtils.timestamp());
    }
}
