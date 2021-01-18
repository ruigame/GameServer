package com.game.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * @author liguorui
 * @date 2018/1/14 14:51
 */
@ChannelHandler.Sharable
public class ByteToWebSocketFrameEncoder extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws  Exception {
        out.add(new BinaryWebSocketFrame(msg).retain());
    }
}
