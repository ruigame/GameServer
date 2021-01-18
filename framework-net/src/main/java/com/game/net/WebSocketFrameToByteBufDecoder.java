package com.game.net;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;

/**
 * @Author: liguorui
 * @Date: 2020/12/2 上午12:01
 */
@ChannelHandler.Sharable
public class WebSocketFrameToByteBufDecoder extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame)msg;
            textWebSocketFrame.retain();
            ctx.writeAndFlush(textWebSocketFrame);
            ReferenceCountUtil.release(msg);
        } else if (msg instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame)msg;
            ctx.fireChannelRead(binaryWebSocketFrame.content());
        }
    }
}
