package com.game.net.packet;

/**
 * @Author: liguorui
 * @Date: 2020/12/1 上午12:44
 */
public interface IPacketCodec {

    void read(Request request, AbstractPacket packet);

    void write(Response response, AbstractPacket packet);
}
