package com.game.net;

import com.game.net.cross.CrossClient;
import com.game.net.packet.ResponsePacket;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2020/11/30 下午11:44
 */
public interface NetServerBuild {

    void clearChannel(Channel channel);

    void clearCrossChannel(Channel channel);

    void afterChannelRead(String platform, int serverId, Channel curChannel, List<Integer> holdServerIdList);

    void fireCrossClientClosr(CrossClient crossClient);

    /**
     * 发送空闲包 RespLoginConflictPacket
     * @return
     */
    ResponsePacket sendIdle();
}
