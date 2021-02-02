package com.game.logic.client.tcp.net;

import com.game.net.BaseFrameDecoder;
import com.game.net.packet.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 纯tcp的client
 * @Author: liguorui
 * @Date: 2021/1/28 下午9:38
 */
public class TcpClientEncoder extends MessageToByteEncoder<Response> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Response msg, ByteBuf out) throws Exception {
        ByteBuf contentBuf = msg.getByteBuf();
        int contentLength = contentBuf.readableBytes();
        out.writeBytes(BaseFrameDecoder.FRAME_MAGIC);
        out.writeInt(0); //sno序列号
        out.writeInt(contentLength); //字节长度
        out.writeBytes(contentBuf, 0, contentLength);
    }
}
