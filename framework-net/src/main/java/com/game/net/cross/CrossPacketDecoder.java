package com.game.net.cross;

import com.game.net.packet.AbstractPacket;
import com.game.net.packet.PacketFactory;
import com.game.net.packet.Request;
import com.game.util.ExceptionUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2020/12/14 下午9:54
 */
@ChannelHandler.Sharable
public class CrossPacketDecoder extends MessageToMessageDecoder<Request> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Request msg, List<Object> out) throws Exception {
        try {
            final AbstractPacket packet = PacketFactory.createPacket(msg);
            out.add(packet);
        } catch (Exception e) { //跨服不能让报错断了连接
            ExceptionUtils.log(e);
        }
    }
}
