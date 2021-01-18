package com.game.base;

import com.game.PacketId;
import com.game.logic.RespLoginConflictPacket;
import com.game.net.ChannelUtils;
import com.game.net.GameStartParams;
import com.game.net.NetServerBuild;
import com.game.net.cross.CrossClient;
import com.game.net.packet.PacketFactory;
import com.game.net.packet.ResponsePacket;
import com.game.util.GameSession;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2020/12/14 下午11:02
 */
public class NetHelper {

    /**
     * GameServerHandler要求的packetid数组
     */
    public static final short[] SERVER_HANDER_EXCLUDE = {PacketId.Base.REQ_LOGIN_AUTH, PacketId.Base.REQ_RECONNECT};

    /**
     * 用于返回客户端包输出，屏蔽以下的频繁包
     * ResponseEncoder的excludePacketIds(频繁协议 例如战报 boss血量广播，玩家经验变化，系统心跳，玩家物品背包，金钱更新等)
     */
    public static final short[] RESPONSE_EXCLUDES = new short[] {

    };

    /**
     * ResponseEncoder的includePacketIds
     */
    public static final short[] RESPONSE_INCLUDES = new short[] {
//            PacketId.Scene.RESP_OBJECT_ENTER_VIEW,  进入可视区域
//            PacketId.Scene.RESP_OBJECT_REMOVE_VIEW,  退出可视区域
//            PacketId.Scene.RESP_PLAYER_MOVE, 返回玩家移动
    };

    /**
     * CrossPacketHandler
     */
    public static final short[] CROSS_PACKET_EXCLUDES = {
//            PacketId.Cross.CROSS_HEARTBEAT, //跨服心跳
//            PacketId.Cross.CROSS_DISPATCH_RESPONSE, //请求跨服分发相应
    };

    /**
     * CrossPingResphandler
     */
    public static final int PING_PACKETID = 1;  //PacketId.Cross.PING; 跨服ping

    /**
     * CrossIdleHandler
     */
    public static final int PONG_PACKETID = 1;  //PacketId.Cross.PONG; 跨服pong

    /**
     * CrossResponseEncoder
     */
    public static final short[] CROSS_RESPONSE_ENCODER_EXCLUDES = {
//            PacketId.Cross.CROSS_HEARTBEAT; 跨服心跳
    };

    public static final GameStartParams gameStartParams = createGameStartParams();

    public static GameStartParams getGameStartParams() {
        return gameStartParams;
    }

    public static GameStartParams createGameStartParams() {
        NetServerBuild netServerBuild = new NetServerBuild() {
            @Override
            public void clearChannel(Channel channel) {
                final GameSession session = ChannelUtils.removeChannelSession(channel);
                if (session == null) {
                    return;
                }
//                Context.getBean(Onlinservice.class).closeInactiveSession(session);
            }

            @Override
            public void clearCrossChannel(Channel channel) {
                ChannelUtils.removeChannelSession(channel);
//                Context.getBean(CrosschannelManager.class).removeServerChannel(channel);
            }

            @Override
            public void afterChannelRead(String platform, int serverId, Channel channel, List<Integer> list) {
//                Channel oldChannel = Context.getBean(CrossChannelManager.class).putServerChannel(platform, serverId, channel, list);
//                if (oldChannel != null) {
//                    oldChannel.close();
//                }
                ListenerHandler.getInstance().fireClientConnectSuc(platform, serverId);
            }

            @Override
            public void fireCrossClientClosr(CrossClient crossClient) {
                ListenerHandler.getInstance().fireCrossClientClose(crossClient);
            }

            @Override
            public ResponsePacket sendIdle() {
                RespLoginConflictPacket packet = PacketFactory.createPacket(PacketId.Utils.RESP_LOGIN_CONFLICT);
                packet.idle();
//                Response response = packet.write();
                return packet;
            }
        };
        GameStartParams gameStartParams = new GameStartParams();
        gameStartParams.setNetServerBuild(netServerBuild);
        gameStartParams.setPingPacketId(NetHelper.PING_PACKETID);
        gameStartParams.setPongResponse(PacketUtils.sendCrossChannelIdle());
        gameStartParams.setResponseEncoderExcludeIds(NetHelper.RESPONSE_EXCLUDES);
        gameStartParams.setResponseEncoderIncludeIds(NetHelper.RESPONSE_INCLUDES);
        gameStartParams.setCrossPacketExcludes(NetHelper.CROSS_PACKET_EXCLUDES);
        gameStartParams.setGameServerPacketIds(NetHelper.SERVER_HANDER_EXCLUDE);
//        gameStartParams.setServerOpen(Context.getBean(Configservice.class).isServerOpen());
        gameStartParams.setGameServerIdleResponse(PacketUtils.sendChannelIdle());
//        gameStartParams.setAdminPatterns(Context.getBean(Configservice.class).getPatterns);
//        gameStartParams.setAdminKey(Context.getBean(Configservice.class).getAdminKey());
//        gameStartParams.setPlatform(Context.getBean(Configservice.class).getplaform());
//        gameStartParams.setServerId(Context.getBean(Configservice.class).getOriServerId());
//        gameStartParams.setServerIds(Context.getBean(Configservice.class).getHoldingServerIds());
        gameStartParams.setCrossPongId(PONG_PACKETID);
        gameStartParams.setCrossIdleResponse(PacketUtils.sendCrossChannelIdle());
        gameStartParams.setCrossResponseEncoderExcludeIds(CROSS_RESPONSE_ENCODER_EXCLUDES);
        return gameStartParams;
    }
}
