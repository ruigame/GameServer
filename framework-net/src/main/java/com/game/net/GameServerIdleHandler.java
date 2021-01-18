package com.game.net;

import com.game.util.GameSession;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SOCKET 空闲清理
 * @Author: liguorui
 * @Date: 2020/12/1 下午11:13
 */
@ChannelHandler.Sharable
public class GameServerIdleHandler extends ChannelDuplexHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerIdleHandler.class);

    private GameStartParams gameStartParams;

    public GameServerIdleHandler(GameStartParams gameStartParams) {
        this.gameStartParams = gameStartParams;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent)evt;
            if (e.state() == IdleState.READER_IDLE) {
                GameSession session = ChannelUtils.getChannelSession(ctx.channel());
                if (session == null) {
                    ChannelFuture future = ctx.channel().writeAndFlush(gameStartParams.getNetServerBuild().sendIdle().write());
                    future.addListener(ChannelFutureListener.CLOSE);
                    LOGGER.warn("{} {} has not session, Channel Idle timeout",
                            ChannelUtils.getAccount(ctx.channel()), ctx.channel().remoteAddress().toString());
                    return;
                }
                session.close(CloseCause.IDLE_TIMEOUT);
                LOGGER.warn("Channel Idle timeout: {} {}", ChannelUtils.getAccount(ctx.channel()), ctx.channel().remoteAddress().toString());
            }
        }
    }
}
