package com.game.net.admin;

import com.game.util.ChannelUtils;
import com.game.util.CloseCause;
import com.game.net.GameStartParams;
import com.game.net.packet.AbstractPacket;
import com.game.net.packet.AdminPacket;
import com.game.net.packet.PacketHandlerService;
import com.game.util.Context;
import com.game.util.GameSession;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: liguorui
 * @Date: 2020/12/14 下午8:56
 */
@ChannelHandler.Sharable
public class AdminPacketHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(AdminPacketHandler.class);

    private final static Logger socketLog = LoggerFactory.getLogger("socketLog");

    private GameStartParams gameStartParams;

    public AdminPacketHandler(GameStartParams gameStartParams) {
        this.gameStartParams = gameStartParams;
    }

    public boolean isInWhiteList(String ip) {
        for (String pattern : gameStartParams.getAdminPatterns()) {
            if (ip.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        socketLog.info("admin ChannelActive {}", ctx.channel());
        if (!ChannelUtils.addChannelSession(ctx.channel(), new GameSession(ctx.channel()))) {
            ctx.channel().close();
            log.error("admin Duplicate Session! IP:{}", ChannelUtils.getIp(ctx.channel()));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        socketLog.info("admin ChannelInactive {} account:{}", ctx.channel(), ChannelUtils.getAccount(ctx.channel()));
        clearChannel(ctx.channel());
    }

    private void clearChannel(Channel channel) {
        ChannelUtils.removeChannelSession(channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final AbstractPacket packet = (AbstractPacket)msg;
        final GameSession session = ChannelUtils.getChannelSession(ctx.channel());
        Preconditions.checkNotNull(session);

        log.debug("Recv admin {}, {}, {}, {}", packet.getCmd(), packet.getClass().getSimpleName(), session.getAccount(), ctx.channel());

        if (packet.isAdmin()) {
            String ip = session.getIp();
            boolean isWhiteList = isInWhiteList(ip);
            if (!isWhiteList) {
                session.close(CloseCause.ADMIN_NOT_WHITE);
                return;
            }
            AdminPacket adminPacket = (AdminPacket)packet;
            int time = adminPacket.getTime();
            String key = adminPacket.getKey();
            String rightKey = DigestUtils.md5Hex(time + gameStartParams.getAdminKey());
            if (!rightKey.equals(key)) {
                session.close(CloseCause.ADMIN_KEY_INVALID);
                return;
            }
            Context.getBean(PacketHandlerService.class).handleAdminPacket(session, packet);
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
