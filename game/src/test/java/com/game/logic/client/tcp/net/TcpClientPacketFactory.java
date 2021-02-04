package com.game.logic.client.tcp.net;

import com.game.net.exception.NoSuchPacketException;
import com.game.net.packet.*;
import org.apache.commons.collections.map.HashedMap;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;

/**
 * @Author: liguorui
 * @Date: 2021/1/28 下午9:05
 */
public class TcpClientPacketFactory {

    private static final Map<Short, Class<? extends AbstractPacket>> PACKET_MAP;
    private static final Map<Short, IPacketCodec> CODEC_MAP;

    static {
        Reflections reflections = new Reflections("com.game.logic");

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Packet.class);
        Map<Short, Class<? extends AbstractPacket>> packets = new HashedMap();
        Map<Short, IPacketCodec> codecMap = new HashedMap();
        for (Class<?> packetClazz : classes) {
            if (!AbstractPacket.class.isAssignableFrom(packetClazz)) {
                continue;
            }
            Packet packet = packetClazz.getAnnotation(Packet.class);
            short packetId = packet.commandId();
            packets.put(packetId, (Class<? extends AbstractPacket>)packetClazz);
            try {
                if (packet.codec()) {
                    Class<?> codecClass = Class.forName(packetClazz.getName() + "Codec");
                    codecMap.put(packetId, (IPacketCodec) codecClass.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        PACKET_MAP = packets;
        CODEC_MAP = codecMap;
    }

    public static Class<?> getPacket(short packetId) {
        return PACKET_MAP.get(packetId);
    }

    public static IPacketCodec getCodec(short packetId) {
        return CODEC_MAP.get(packetId);
    }

    public static <T extends AbstractPacket> T createPacket(short packetId) {
        Class<? extends AbstractPacket> clazz = PACKET_MAP.get(packetId);
        if (clazz == null) {
            throw new NoSuchPacketException(packetId);
        }
        AbstractPacket newInstance;
        try {
            newInstance = clazz.newInstance();
            newInstance.setCmd(packetId);
            return (T)newInstance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean tryEncode(AbstractPacket packet, Response response) {
        short packetId = packet.getCmd();
        IPacketCodec codec = CODEC_MAP.get(packetId);
        if (codec != null) {
            codec.write(response, packet);
            return true;
        }
        return false;
    }

    public static boolean tryDecode(AbstractPacket packet, Request request) {
        short packetId = packet.getCmd();
        IPacketCodec codec = CODEC_MAP.get(packetId);
        if (codec != null) {
            codec.read(request, packet);
            return true;
        }
        return false;
    }
}
