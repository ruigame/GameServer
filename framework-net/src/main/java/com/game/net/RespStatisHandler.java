package com.game.net;

import com.game.net.packet.ByteBufResponse;
import com.game.util.ExceptionUtils;
import com.game.util.GameSession;
import io.netty.channel.*;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午1:43
 */
@ChannelHandler.Sharable
public class RespStatisHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBufResponse) {
            ByteBufResponse response = (ByteBufResponse)msg;
//            Context.getPacketStatisticService().statisticResp(response);
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        GameSession session = ChannelUtils.getChannelSession(ctx.channel());
        String msg = String.format("exceptionCaught! IP: %s Account;%s Status:%s",
                ctx.channel(), session == null ? "" : session.getAccount(),
                session == null ? "unkonwn" : session.getStatus());
        ExceptionUtils.log(msg, cause);
        Channel channel = ctx.channel();
        if (channel.isOpen() || channel.isActive()) {
            channel.close();
        }
    }
}
