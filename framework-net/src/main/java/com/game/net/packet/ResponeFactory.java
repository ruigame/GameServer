package com.game.net.packet;

/**
 * @Author: liguorui
 * @Date: 2020/12/1 上午12:17
 */
public class ResponeFactory {

    public static Response createResponse(int packetId) {
        return new ByteBufResponse(packetId);
    }

    public static Response createResponse(int packetId, int maxCapacity) {
        return new ByteBufResponse(packetId, maxCapacity);
    }
}
