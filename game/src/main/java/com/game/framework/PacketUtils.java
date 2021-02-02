package com.game.framework;

import com.game.PacketId;
import com.game.logic.RespLoginConflictPacket;
import com.game.logic.common.PlayerActor;
import com.game.logic.cross.packet.CrossPingPacket;
import com.game.logic.player.packet.resp.RespExpPacket;
import com.game.logic.player.packet.resp.RespLevelPacket;
import com.game.net.packet.PacketFactory;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;
import com.game.util.CloseCause;
import com.game.util.GameSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 上午12:07
 */
public class PacketUtils {

    /**
     * 玩家经验
     * @param playerActor
     * @param diff
     */
    public static void sendExpPacket(PlayerActor playerActor, long diff) {
        RespExpPacket packet = PacketFactory.createPacket(PacketId.Role.RESP_EXP);
        packet.init(playerActor.getPlayerExp().getExp(), diff);
        playerActor.sendPacket(packet);
    }

    /**
     * 发送角色等级
     * @param playerActor
     */
    public static void sendLevel(PlayerActor playerActor) {
        RespLevelPacket packet = PacketFactory.createPacket(PacketId.Role.RESP_LEVEL);
        packet.init(playerActor.getLevel(), playerActor.getPlayerExp().getMaxExp());
        playerActor.sendPacket(packet);
    }

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

    public static ChannelFuture sendWithFuture(Channel channel, ResponsePacket packet) {
        Response resp = packet.write();
        return channel.writeAndFlush(resp);
    }

    public static void sendAndClose(GameSession session, ResponsePacket packet) {
        ChannelFuture future = sendWithFuture(session.getChannel(), packet);
        future.addListener(ChannelFutureListener.CLOSE);
    }
}
