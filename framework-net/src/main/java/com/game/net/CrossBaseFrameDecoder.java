package com.game.net;

import com.game.net.packet.ByteBufRequest;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 该类读取magic内容和剩下包长度值，返回给下个handler内容为协议头和协议内容
 * @Author: liguorui
 * @Date: 2020/12/1 下午10:59
 */
public class CrossBaseFrameDecoder extends ByteToMessageDecoder {

    private int bodySize = 0;

    public static final int maxBodySize = 16 * 1024 * 1024; //最大16M

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (bodySize ==0) {
            if (in.readableBytes() < 4) {
                return;
            }
            bodySize = in.readInt();
            Preconditions.checkArgument(bodySize <= maxBodySize, "超过最大字节 %s", bodySize);
        }
        if (in.readableBytes() < bodySize) {
            return;
        }
        try {
            ByteBuf frameBuf = in.readRetainedSlice(bodySize);
            out.add(new ByteBufRequest(frameBuf));
        } finally {
            bodySize = 0;
        }
    }
}
