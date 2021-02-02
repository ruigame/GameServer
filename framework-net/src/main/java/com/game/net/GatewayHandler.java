package com.game.net;

import com.game.util.ChannelUtils;
import com.game.util.GameSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * @Author: liguorui
 * @Date: 2020/12/1 下午11:22
 */
public class GatewayHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!ChannelUtils.addChannelSession(ctx.channel(), new GameSession(ctx.channel()))) {
            ctx.channel().close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        byte[]bs = new byte[3];
        buf.getBytes(0, bs);
        if (isHttp(bs)) {
            ChannelPipeline pipeline = ctx.pipeline();
            pipeline.addLast("http-codec", new HttpServerCodec());
            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
            pipeline.addLast("http-codec", new WebSocketServerProtocolHandler("/"));
        }
    }

    private boolean isHttp(byte[]bs) {
        return bs[0] == 'G' && bs[1] == 'E' && bs[2] == 'T';
    }
}
