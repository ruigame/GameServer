package com.game.logic.client.tcp;

import com.game.net.packet.ByteBufRequest;
import com.game.net.packet.Request;
import com.game.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @Author: liguorui
 * @Date: 2021/1/28 下午9:29
 */
public class RequestBuilder {

    private ByteBuf byteBuf;

    public static RequestBuilder create(ByteBuf byteBuf) {
        RequestBuilder data = new RequestBuilder();
        data.byteBuf = byteBuf;
        return data;
    }

    public RequestBuilder() {
        byteBuf = Unpooled.buffer(64);
        byteBuf.writeShort(0);
    }

    public RequestBuilder(int packetId) {
        this();
        setPacketId(packetId);
    }

    public RequestBuilder setPacketId(int packetId) {
        byteBuf.setShort(0, packetId);
        return this;
    }

    public Request build() {
        return new ByteBufRequest(byteBuf);
    }

    public RequestBuilder writeByte(int value) {
        byteBuf.writeByte(value);
        return this;
    }

    public RequestBuilder writeShort(int value) {
        byteBuf.writeShort(value);
        return this;
    }

    public RequestBuilder writeInt(int value) {
        byteBuf.writeInt(value);
        return this;
    }

    public RequestBuilder writeLong(long value) {
        byteBuf.writeLong(value);
        return this;
    }

    public RequestBuilder writeBoolean(boolean value) {
        byteBuf.writeBoolean(value);
        return this;
    }

    public RequestBuilder writeString(String value) {
        ByteBufUtils.writeString(byteBuf, value);
        return this;
    }


    public static RequestBuilder create() {
        return new RequestBuilder();
    }
}
