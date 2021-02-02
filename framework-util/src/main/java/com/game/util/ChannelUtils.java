package com.game.util;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 9:01 下午
 */
public class ChannelUtils {

    private static final AttributeKey<GameSession> SESSION_KEY = AttributeKey.valueOf("session-key");

    /**
     * 添加会话
     * @param channel
     * @param session
     * @return
     */
    public static final boolean addChannelSession(Channel channel, GameSession session) {
        Attribute<GameSession> sessionAttr = channel.attr(SESSION_KEY);
        return sessionAttr.compareAndSet(null, session);
    }

    /**
     * 替换会话
     * @param channel
     * @param session
     */
    public static final void replaceChannelSession(Channel channel, GameSession session) {
        Attribute<GameSession> sessionAttr = channel.attr(SESSION_KEY);
        sessionAttr.set(session);
    }

    /**
     * 移除会话
     * @param channel
     * @return
     */
    public static final GameSession removeChannelSession(Channel channel) {
        Attribute<GameSession> sessionAttr = channel.attr(SESSION_KEY);
        if (sessionAttr == null) {
            return null;
        }
        return sessionAttr.getAndSet(null);
    }

    public static final GameSession getChannelSession(Channel channel) {
        Attribute<GameSession> sessionAttr = channel.attr(SESSION_KEY);
        return sessionAttr.get();
    }

    /**
     * 获取ip地址
     * @param channel
     * @return
     */
    public static final String getIp(Channel channel) {
        return ((InetSocketAddress)channel.remoteAddress()).getAddress().toString().substring(1);
    }

    public static final String getAccount(Channel channel) {
        GameSession session = getChannelSession(channel);
        if (session != null) {
            return session.getAccount();
        }
        return "";
    }

    public static void clearAndCloseChannel(Channel channel) {
        removeChannelSession(channel);
        if (channel.isActive()) {
            channel.close();
        }
    }
}
