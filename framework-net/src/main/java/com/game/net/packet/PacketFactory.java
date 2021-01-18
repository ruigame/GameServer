package com.game.net.packet;

import com.game.util.GameSession;
import com.game.net.exception.NoSuchPacketException;
import com.game.net.exception.PacketDecodeException;
import com.game.util.ExceptionUtils;
import com.google.common.collect.Lists;
import com.koloboke.collect.map.ShortObjMap;
import com.koloboke.collect.map.hash.HashShortObjMaps;

import java.util.*;

/**
 * 包工厂
 *
 * @author liguorui
 * @date 2016-01-06
 */
public class PacketFactory {


    private static final PacketFactory factory = new PacketFactory();

    private static ShortObjMap<Class<? extends AbstractPacket>> packets;

    private static ShortObjMap<IPacketCodec> codecs = HashShortObjMaps.newImmutableMap(Collections.emptyMap());

    private static short[] reqPacketIds;

    private static short[] respPacketIds;

    public static PacketFactory getInstance() {
        return factory;
    }

    /**
     * 通过消息ID创建一个包
     * @param packetId
     * @param <T>
     * @return
     */
    public static <T extends AbstractPacket> T createPacket(short packetId) {
        Class<? extends AbstractPacket> clazz = packets.get(packetId);
        if (clazz == null) {
            throw new NoSuchPacketException(packetId);
        }
        AbstractPacket newInstance;
        try {
            newInstance = clazz.newInstance();
            newInstance.setCmd(packetId);
            return (T) newInstance;
        } catch (Exception e) {
            ExceptionUtils.log(e);
        }
        throw new NoSuchPacketException(packetId);
    }

    public static <T extends AbstractPacket> T createPacket(Request request, GameSession session) throws Exception {
        short packetId = request.getPacketId();
        try {
            AbstractPacket createPacket = createPacket(packetId);
            createPacket.beforeRead(request);
            if (!tryDecode(createPacket, request)) {
                createPacket.read(request);
            }
            return (T)createPacket;
        } catch (Exception e) {
            ExceptionUtils.log("PacketId:{}", packetId, e);
            throw new PacketDecodeException("PacketId:" + packetId, e);
        }
    }

    public static <T extends AbstractPacket> T createPacket(Request request) throws Exception {
        return createPacket(request, null);
    }

    public static void init(ShortObjMap<Class<? extends AbstractPacket>> packets) {
        PacketFactory.packets = HashShortObjMaps.newImmutableMap(packets);
        init0();

    }

    public static void initCodec(ShortObjMap<IPacketCodec> codecs) {
        PacketFactory.codecs = HashShortObjMaps.newImmutableMap(codecs);
    }

    public static void init0() {
        try {
            List<Short> req = Lists.newArrayList();
            List<Short> resp = Lists.newArrayList();
            for (Map.Entry<Short, Class<? extends AbstractPacket>> entry : packets.entrySet()) {
                short cmdId = entry.getKey();
                Class<?> packetClass = entry.getValue();
                if (packetClass.getSimpleName().startsWith("Req")) {
                    req.add(cmdId);
                } else {
                    resp.add(cmdId);
                }
            }

            reqPacketIds = new short[req.size()];
            for (int i = 0; i < req.size(); i++) {
                reqPacketIds[i] = req.get(i);
            }
            respPacketIds = new short[resp.size()];
            for (int i = 0; i < resp.size(); i++) {
                respPacketIds[i] = resp.get(i);
            }
        } catch (Exception e) {
            ExceptionUtils.log(e);
        }
    }

    public static ShortObjMap<Class<? extends AbstractPacket>> getPackets() {
        return packets;
    }

    public static Class<? extends AbstractPacket> getPacketClassByCommonId(short cmdId) {
        return PacketFactory.packets.get(cmdId);
    }

    public static short[] getReqPacketIds() {
        return reqPacketIds;
    }

    public static short[] getRespPacketIds() {
        return respPacketIds;
    }

    public static boolean tryDecode(AbstractPacket packet, Request request) {
        short packetId = packet.getCmd();
        IPacketCodec codec = codecs.get(packetId);
        if (codec != null) {
            codec.read(request, packet);
            return true;
        }
        return false;
    }

    public static boolean tryEncode(AbstractPacket packet, Response response) {
        short packetId = packet.getCmd();
        IPacketCodec codec = codecs.get(packetId);
        if (codec != null) {
            codec.write(response, packet);
            return true;
        }
        return false;
    }

    public static boolean containsCodec(short protoId) {
        return codecs.containsKey(protoId);
    }

    public static Set<Short> getCodesIds() {
        return new HashSet<>(codecs.keySet());
    }
}
