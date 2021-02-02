package com.game.net.packet;

import com.game.net.exception.PacketIdConflictException;
import com.game.util.ExceptionUtils;
import com.google.common.base.Preconditions;
import com.koloboke.collect.map.ShortObjMap;
import com.koloboke.collect.map.hash.HashShortObjMaps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * @Author: liguorui
 * @Date: 2021/2/2 下午8:48
 */
@Component
public class PacketScanner implements InitializingBean, ApplicationContextAware {

    private ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> beans = ctx.getBeansWithAnnotation(Packet.class);
        ShortObjMap<Class<? extends AbstractPacket>> packets = HashShortObjMaps.newUpdatableMap();
        ShortObjMap<IPacketCodec> codecs = HashShortObjMaps.newUpdatableMap();
        for (Object bean : beans.values()) {
            AbstractPacket packet = (AbstractPacket)bean;
            checkPacketName(packet);
            Packet annotation = bean.getClass().getAnnotation(Packet.class);
            short commandId = annotation.commandId();
            if (packets.containsKey(commandId)) {
                throw new PacketIdConflictException(commandId, Objects.requireNonNull(packets.get(commandId)), packet.getClass());
            }
            packets.put(commandId, packet.getClass());
            boolean isCodec = annotation.codec();
            if (isCodec) {
                try {
                    Class<?> codecClass = Class.forName(bean.getClass().getName() + "Codec");
                    codecs.put(commandId, (IPacketCodec) (codecClass.newInstance()));
                } catch (Exception e) {
                    ExceptionUtils.log(e);
                }
            }
        }

        PacketFactory.init(packets);
        PacketFactory.initCodec(codecs);
    }

    private void checkPacketName(AbstractPacket packet) {
        String name = packet.getClass().getName();
        String sumpleName = packet.getClass().getSimpleName();

        //检查后缀
        Preconditions.checkArgument(sumpleName.endsWith("Packet"), "packet name invalid class=%s", name);

        //检查前缀
        if (packet instanceof RequestPacket) {
            Preconditions.checkArgument(sumpleName.startsWith("Req"), "request packet name invalid class=%s", name);
        } else if (packet instanceof ResponsePacket) {
            Preconditions.checkArgument(sumpleName.startsWith("Resp"), "response packet name invalid class=%s", name);
        } else if (packet instanceof AdminPacket) {
            Preconditions.checkArgument(sumpleName.startsWith("ReqAdmin"), "admin packet name invalid class=%s", name);
        } else if (packet instanceof CrossPacket) {
            Preconditions.checkArgument(sumpleName.startsWith("Cross"), "cross packet name invalid class=%s", name);
        }
        //目前只约定这四种类型
    }
}
