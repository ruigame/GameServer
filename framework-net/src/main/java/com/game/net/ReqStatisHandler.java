package com.game.net;

import com.game.net.packet.ByteBufRequest;
import com.game.util.Context;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午1:40
 */
@ChannelHandler.Sharable
public class ReqStatisHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBufRequest) {
            ByteBufRequest request = (ByteBufRequest)msg;
//            Context.getBean(PacketStatisticService.class).statisticReq(request);
        }
        super.channelRead(ctx, msg);
    }
}
