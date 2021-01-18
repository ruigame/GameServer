package com.game.net;

import com.game.net.exception.GameAuthException;
import com.game.net.packet.AbstractPacket;
import com.game.net.packet.PacketHandlerService;
import com.game.util.Context;
import com.game.util.GameSession;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理未进入场景前的所有业务逻辑，执行线程为netty的IO线程，所有业务逻辑在此处理
 * @Author: liguorui
 * @Date: 2020/11/30 下午11:59
 */
@ChannelHandler.Sharable
public class GameServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger socketLog = LoggerFactory.getLogger("socketLog");

    private GameStartParams gameStartParams;

    public GameServerHandler(GameStartParams gameStartParams) {
        this.gameStartParams = gameStartParams;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        socketLog.info("ChannelActive {}", ctx.channel());
        if (!ChannelUtils.addChannelSession(ctx.channel(), new GameSession(ctx.channel()))) {
            ctx.channel().close();
            socketLog.info("Duplicate Session! IP:{}", ChannelUtils.getIp(ctx.channel()));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        socketLog.info("channelInactive {} account:{}", ctx.channel(), ChannelUtils.getAccount(ctx.channel()));
        clearChannel(ctx.channel());
    }

    public void clearChannel(Channel channel) {
        gameStartParams.getNetServerBuild().clearChannel(channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final AbstractPacket packet = (AbstractPacket)msg;
        final GameSession session = ChannelUtils.getChannelSession(ctx.channel());
        Preconditions.checkNotNull(session);

        socketLog.debug("Recv {}, {}, {}, {}", packet.getCmd(), packet.getClass().getSimpleName(), session.getAccount(), ctx.channel());

        if (!session.isAuthed() && !ArrayUtils.contains(gameStartParams.getGameServerPacketIds(), packet.getCmd())) {
            throw new GameAuthException(String.format("PacketId: %s, IP:%s, NOT Yet Auth", packet.getCmd(), session.getIp()));
        }

        if(!gameStartParams.isServerOpen()) {
            socketLog.info("Packet NOT Handle due to Server Close {}, {}, {}, {}", packet.getCmd(), packet.getClass().getSimpleName(), session.getAccount(), ctx.channel());
            return;
        }
        Context.getBean(PacketHandlerService.class).handle(session, packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
