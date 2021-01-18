package com.game.net.cross;

import com.game.net.GameStartParams;
import com.game.net.packet.AbstractPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: liguorui
 * @Date: 2020/12/14 下午9:57
 */
@ChannelHandler.Sharable
public class CrossPingRespHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrossPingRespHandler.class);

    private GameStartParams gameStartParams;

    public CrossPingRespHandler(GameStartParams gameStartParams) {
        this.gameStartParams = gameStartParams;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof AbstractPacket) {
            AbstractPacket packet = (AbstractPacket)msg;
            if (packet.getCmd() == gameStartParams.getPingPacketId()) {
                ctx.channel().writeAndFlush(gameStartParams.getPongResponse());
                LOGGER.info("收到PING，回复PONG {}", ctx.channel());
                return;
            }
        }
        super.channelRead(ctx, msg);
    }
}
