package com.game.net.packet;

import com.game.util.GameSession;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/12/1 下午10:36
 */
@Component
public class PacketHandlerFactory implements InitializingBean, ApplicationContextAware {

    private ApplicationContext ctx;

    private Map<PacketType, IPacketHandler> type2Handler = Maps.newHashMap();

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<PacketType, IPacketHandler> type2Handler = Maps.newHashMap();
        Map<String, IPacketHandler> builders = ctx.getBeansOfType(IPacketHandler.class);
        for (IPacketHandler packetHandler : builders.values()) {
            type2Handler.put(packetHandler.getPacketType(), packetHandler);
        }
        this.type2Handler = type2Handler;
    }

    public void handlePacket(PacketType packetType, GameSession gameSession, AbstractPacket packet, PacketHandlerWrapper wrapper) {
        IPacketHandler packetHandler = type2Handler.get(packetType);
        if (packetHandler != null) {
            packetHandler.handlePacket(gameSession, packet, wrapper);
        }
    }
}
