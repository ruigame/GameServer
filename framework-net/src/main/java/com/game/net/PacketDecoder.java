package com.game.net;

import com.game.net.packet.AbstractPacket;
import com.game.net.packet.PacketFactory;
import com.game.net.packet.Request;
import com.game.util.GameSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2020/12/1 下午11:28
 */
@ChannelHandler.Sharable
public class PacketDecoder extends MessageToMessageDecoder<Request> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Request msg, List<Object> out) throws Exception {
        final GameSession session = ChannelUtils.getChannelSession(ctx.channel());
        final AbstractPacket packet = PacketFactory.createPacket(msg, session);
        out.add(packet);
    }
}
