package com.game.base;

import com.game.PacketId;
import com.game.logic.RespLoginConflictPacket;
import com.game.logic.cross.packet.CrossPingPacket;
import com.game.net.CloseCause;
import com.game.net.packet.PacketFactory;
import com.game.net.packet.Response;
import com.game.util.GameSession;

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
     * 发送账号重登陆
     * @param session
     * @param ip
     */
    public static void sendLoginConflict(GameSession session, String ...ip) {
        RespLoginConflictPacket packet = PacketFactory.createPacket(PacketId.Utils.RESP_LOGIN_CONFLICT);
        packet.dulicateLogin();
        Response response = packet.write();
        GameSessionHelper.writeAndClose(session, response, CloseCause.Duplicate_login, ip);
    }

    /**
     * 封禁
     * @param session
     */
    public static void sendForbid(GameSession session) {
        if (session == null) {
            return;
        }
        RespLoginConflictPacket packet = PacketFactory.createPacket(PacketId.Utils.RESP_LOGIN_CONFLICT);
        packet.forbid();
        Response response = packet.write();
        GameSessionHelper.writeAndClose(session, response, CloseCause.FORBID_ROLE);
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
