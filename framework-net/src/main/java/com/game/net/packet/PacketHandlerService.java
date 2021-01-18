package com.game.net.packet;

import com.game.util.GameSession;
import com.game.util.Context;
import com.game.util.ExceptionUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @author liguorui
 * @date 2018/1/7 21:06
 */
@Component
public class PacketHandlerService implements InitializingBean, ApplicationContextAware {

    private ApplicationContext ctx;

    private Map<Class<?>, PacketHandlerWrapper> packetHandlers;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> handlers = ctx.getBeansWithAnnotation(PacketHandler.class);
        Map<Class<?>, PacketHandlerWrapper> packetHandlers = Maps.newHashMap();
        for (Object handler : handlers.values()) {
            Class<?>[] interfaces = handler.getClass().getInterfaces();
            for (Class<?> interf : interfaces) {
                if (!interf.isAnnotationPresent(PacketHandler.class)) {
                    continue;
                }
                Method[] methods = interf.getMethods();
                for (Method method : methods) {
                    if (isPacketMethod(method)) {
                        PacketHandlerWrapper wrapper = new PacketHandlerWrapper(handler, method);
                        PacketHandlerWrapper oldWrapper = packetHandlers.put(getPacketClass(method), wrapper);
                        if (oldWrapper != null) {
                            throw new RuntimeException(MessageFormat.format("接收包：{0} 方法重复，{1} {2}",
                                    getPacketClass(method), oldWrapper.getMethod(), wrapper.getMethod()));
                        }
                    }
                }
            }
        }
        this.packetHandlers = packetHandlers;
    }

    private boolean isPacketMethod(Method method) {
        Class<?>[] paramType = method.getParameterTypes();
        for (Class<?> paramClass : paramType) {
            if (AbstractPacket.class.isAssignableFrom(paramClass)) {
                return true;
            }
        }
        return false;
    }

    private Class<?> getPacketClass(Method method) {
        Class<?>[] paramType = method.getParameterTypes();
        for (Class<?> paramClass : paramType) {
            if (AbstractPacket.class.isAssignableFrom(paramClass)) {
                return paramClass;
            }
        }
        return null;
    }

    public void handle(final GameSession session, final AbstractPacket packet) {
        final PacketHandlerWrapper wrapper = packetHandlers.get(packet.getClass());
        Preconditions.checkNotNull(wrapper, "NoPacketHandler for %", packet.getClass().getSimpleName());
        if (packet instanceof RequestBeforeLoginPacket) {
            Context.getBean(PacketHandlerFactory.class).handlePacket(PacketType.BEFORELOGIN, session, packet, wrapper);
//            handleSessionPacket(wrapper, session, packet);
            return;
        }
        if (packet instanceof AdminPacket) {
            ExceptionUtils.log("游戏端口收到后台协议:{}", packet.getCmd());
//            Context.getAdminService().execute();
            return;
        }
        if (packet instanceof RequestPacket) {
            Context.getBean(PacketHandlerFactory.class).handlePacket(PacketType.PLAYER, session, packet, wrapper);
            return;
        }
        if (packet instanceof  CrossPacket) {
            ExceptionUtils.log("游戏端口收到跨服协议:{}", packet.getCmd());
            return;
        }
    }

    public void handleAdminPacket(final GameSession session, final AbstractPacket packet) {
        final PacketHandlerWrapper wrapper = packetHandlers.get(packet.getClass());
        Preconditions.checkNotNull(wrapper, "NoPacketHandler for %s", packet.getClass().getSimpleName());
        if (packet instanceof AdminPacket) {
            Context.getBean(PacketHandlerFactory.class).handlePacket(PacketType.ADMIN, session, packet, wrapper);
        } else {
            ExceptionUtils.log("后台端口收到异常协议:{}", packet.getCmd());
        }
    }

    public void handleCrossPacket(CrossPacket packet) {
        final PacketHandlerWrapper wrapper = packetHandlers.get(packet.getClass());
        Preconditions.checkNotNull(wrapper, "NoPacketHandler for %s", packet.getClass().getSimpleName());
        Context.getBean(PacketHandlerFactory.class).handlePacket(PacketType.CROSS, null, packet, wrapper);
    }
}
