package com.game.base;

import com.game.logic.common.ConfigService;
import com.game.logic.common.OnlineService;
import com.game.logic.player.domain.ResourceType;
import com.game.net.CloseCause;
import com.game.net.NullChannel;
import com.game.net.packet.AbstractPacket;
import com.game.net.packet.Response;
import com.game.util.Context;
import com.game.util.GameSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午3:13
 */
public class GameSessionHelper {

    public static void sendPacket(GameSession session, AbstractPacket packet) {
        if (packet == null) {
            return;
        }

        Response response = packet.write();
        if (response == null) {
            return;
        }
        write(response, session);
    }

    public static boolean write(Response response, GameSession session) {
        Channel channel = session.getChannel();
        AtomicBoolean reflushChannelSchedule = session.getReflushChannelSchedule();
        if (!session.isActive()) {
            Context.getBean(OnlineService.class).sessionClose(session);
            return false;
        }
        if (response != null && !(channel instanceof NullChannel)) {
            if (Context.getBean(ConfigService.class).isFlushStatus()) { //是否立即刷新
                channel.writeAndFlush(response);
            } else {
                ChannelFuture writeFuture = channel.write(response);
                if (reflushChannelSchedule.compareAndSet(false, true)) {
                    scheduleFlush(writeFuture, session);
                }
            }
            return true;
        }
        return false;
    }

    private static void scheduleFlush(final ChannelFuture writeFuture, GameSession session) {
        Channel channel = session.getChannel();
        AtomicBoolean reflushChannelSchedule = session.getReflushChannelSchedule();
        channel.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                if (reflushChannelSchedule.compareAndSet(true, false)) {
                    channel.flush();
                }
            }
        }, 5, TimeUnit.MILLISECONDS);
    }

    /**
     * 把Response数据立即写，完成后关闭channel，如果写超时就关闭（100毫秒超时）
     * @param session
     * @param response
     * @param cause
     * @param ip
     */
    public static void writeAndClose(GameSession session, Response response, final CloseCause cause, final String... ip) {
        Channel channel = session.getChannel();
        if (response != null && !(channel instanceof NullChannel)) {
            final ChannelFuture future = channel.writeAndFlush(response);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    session.close(cause, ip);
                }
            });
            channel.eventLoop().schedule(new Runnable() {
                @Override
                public void run() {
                    if (!future.isDone()) {
                        session.close(cause, ip);
                    }
                }
            }, 100, TimeUnit.MILLISECONDS);
        }
    }

    public static Response writeAttr(Response response, Map<ResourceType, Long> attrs) {
        response.writeShort(attrs.size());
        for (Map.Entry<ResourceType, Long> entry : attrs.entrySet()) {
            response.writeShort(entry.getKey().getCode());
            response.writeLong(entry.getValue());
        }
        return response;
    }
}
