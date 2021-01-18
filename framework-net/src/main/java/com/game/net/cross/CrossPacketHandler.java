package com.game.net.cross;

import com.game.net.ChannelUtils;
import com.game.net.GameStartParams;
import com.game.net.packet.AbstractPacket;
import com.game.net.packet.CrossPacket;
import com.game.net.packet.PacketHandlerService;
import com.game.util.Context;
import com.game.util.ExceptionUtils;
import com.game.util.GameSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: liguorui
 * @Date: 2020/12/14 下午9:37
 */
@ChannelHandler.Sharable
public class CrossPacketHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger("cross");

    private final static Logger socketLog = LoggerFactory.getLogger("socketLog");

    private GameStartParams gameStartParams;

    public CrossPacketHandler(GameStartParams gameStartParams) {
        this.gameStartParams = gameStartParams;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        socketLog.info("cross ChannelActive {}", ctx.channel());
        if (!ChannelUtils.addChannelSession(ctx.channel(), new GameSession(ctx.channel()))) {
            ctx.channel().close();
            log.error("cross Duplicate Session! IP:{}", ChannelUtils.getIp(ctx.channel()));
        }
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        socketLog.info("cross ChannelInactive {} account:{}", ctx.channel(), ChannelUtils.getAccount(ctx.channel()));
        clearChannel(ctx.channel());
        ctx.fireChannelActive();
    }

    private void clearChannel(Channel channel) {
        gameStartParams.getNetServerBuild().clearCrossChannel(channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final AbstractPacket packet = (AbstractPacket)msg;
        if (ArrayUtils.contains(gameStartParams.getCrossPacketExcludes(), packet.getCmd())) {
            log.debug("cross Recv {}, {}, {}", packet.getCmd(), packet.getClass().getSimpleName(), ctx.channel());
        } else {
            log.info("cross Recv {}, {}, {}", packet.getCmd(), packet.getClass().getSimpleName(), ctx.channel());
        }

        if (packet.isCross()) {
            try {
                Context.getBean(PacketHandlerService.class).handleCrossPacket((CrossPacket) packet);
            } catch (Exception e) {
                ExceptionUtils.log(e);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        GameSession session = ChannelUtils.getChannelSession(ctx.channel());
        String msg = String.format("exceptionCaught! %s Account:%s status:%s ", ctx.channel(), session == null ? "" : session.getAccount(),
                session == null ? "unknow" : session.getStatus());
        socketLog.error(msg, cause);
        Channel channel = ctx.channel();
        if (channel.isOpen() || channel.isActive()) {
            channel.close();
        }
    }
}
