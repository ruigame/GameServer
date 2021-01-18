package com.game.base;

import com.game.PacketId;
import com.game.logic.RespLoginConflictPacket;
import com.game.logic.cross.packet.CrossPingPacket;
import com.game.net.packet.PacketFactory;
import com.game.net.packet.Response;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 上午12:07
 */
public class PacketUtils {

    /**
     * 发送连接长期空闲
     * @return
     */
    public static Response sendChannelIdle() {
        RespLoginConflictPacket packet = PacketFactory.createPacket(PacketId.Utils.RESP_LOGIN_CONFLICT);
        packet.idle();
        Response response = packet.write();
        return response;
    }

    /**
     * 发送跨服连接长期空闲
     * @return
     */
    public static Response sendCrossChannelIdle() {
        CrossPingPacket pingPacket = PacketFactory.createPacket(PacketId.Cross.CROSS_PING);
        return pingPacket.write();
    }
}
