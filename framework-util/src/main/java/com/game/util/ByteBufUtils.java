package com.game.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

/**
 * @Author: liguorui
 * @Date: 2020/11/30 下午8:56
 */
public class ByteBufUtils {

    public static final long MAX_POSITION_INTEGER = 0xffffffffl;

    public static final long MAX_48BIT = (1L << 47);

    public static final long MAX_48BIT_NEGATIVE = -MAX_48BIT;

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    /**
     * 写字符串，32767个字节
     * @param buffer
     * @param str
     */
    public static void writeString(ByteBuf buffer, String str) {
        if (str != null) {
            byte[] content = str.getBytes(UTF8_CHARSET);
            buffer.writeShort(content.length);
            buffer.writeBytes(content);
        } else {
            buffer.writeShort(0);
        }
    }

    public static String readString(ByteBuf buffer) {
        short length = buffer.readShort();
        byte[] content = new byte[length];
        buffer.readBytes(content);
        return new String(content, UTF8_CHARSET);
    }

    /**
     * 写字符串，32767个字节
     * @param buffer
     * @param str
     */
    public static void writeLongString(ByteBuf buffer, String str) {
        if (str != null) {
            byte[] content = str.getBytes(UTF8_CHARSET);
            buffer.writeInt(content.length);
            buffer.writeBytes(content);
        } else {
            buffer.writeInt(0);
        }
    }

    public static String readLongString(ByteBuf buffer) {
        int length = buffer.readInt();
        byte[] content = new byte[length];
        buffer.readBytes(content);
        return new String(content, UTF8_CHARSET);
    }
}
