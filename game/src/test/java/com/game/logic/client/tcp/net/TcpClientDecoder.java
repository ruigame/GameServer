package com.game.logic.client.tcp.net;

import com.game.logic.client.tcp.RequestBuilder;
import com.game.net.packet.AbstractPacket;
import com.game.net.packet.Request;
import com.game.net.packet.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2021/1/28 下午9:25
 */
public class TcpClientDecoder extends ReplayingDecoder<Response> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.readInt();
        in.readInt(); //respSN
        Request request = RequestBuilder.create(in).build();
        short packetId = request.getPacketId();
        AbstractPacket packet = TcpClientPacketFactory.createPacket(packetId);
        TcpClientPacketFactory.tryDecode(packet, request);
        out.add(packet);
    }
}
