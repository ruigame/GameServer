package com.game.net;

import com.game.net.packet.ByteBufRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.Arrays;
import java.util.List;

/**
 * 该类读取magic内容和剩下包长度值，返回个i下一个hadnler内容为协议头和协议内容
 * @author liguorui
 * @date 2018/1/14 14:54
 */
public class BaseFrameDecoder extends ByteToMessageDecoder {

    public static final int MAX_BODY_SIZE = 10240;
    public static final byte[] FRAME_MAGIC = {9,5,2,7};
    private static final int FRAME_HEAD_BODY_SIZE = 8;
    private int frameStatus = 0;
    private int bodySize = 0;
    private static final int FRAME_STATUS_NEED_HEADER = 0;
    private static final int FRAME_STATUS_NEED_BODY = 1;
    private static final int FRAME_STATUS_NEED_CROSS_DOMAIN = 2;

    public static boolean isMagic(byte[] magics) {
        return Arrays.equals(magics, FRAME_MAGIC);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (frameStatus) {
            case FRAME_STATUS_NEED_HEADER:
                if (in.readableBytes() < FRAME_MAGIC.length + FRAME_HEAD_BODY_SIZE) {
                    return;
                }

                byte[] magics = new byte[FRAME_MAGIC.length];
                in.readBytes(magics);
                if (!isMagic(magics)) {
                    String hexDump = ByteBufUtil.hexDump(in);
                    in.skipBytes(in.readableBytes());
                    throw new CorruptedFrameException(String.format("FrameMagicHeadError : magics=%s,HexDump=%s",
                                Arrays.toString(magics), hexDump));
                }
                in.readInt();
                bodySize = in.readInt();
                frameStatus = FRAME_STATUS_NEED_BODY;
            case FRAME_STATUS_NEED_BODY:
                if (bodySize > MAX_BODY_SIZE) {
                    throw new RuntimeException(String.format("bodySize[%s] too large!", bodySize));
                }
                if (in.readableBytes() < bodySize) {
                    return;
                }

                ByteBuf frameBuf = in.readRetainedSlice(bodySize);
//                frameBuf.writeBytes(in, bodySize);
                frameStatus = FRAME_STATUS_NEED_HEADER;
                out.add(new ByteBufRequest(frameBuf));
                return;

            case FRAME_STATUS_NEED_CROSS_DOMAIN:
                in.clear();
                return;

                default:
                    throw new Error("Decode Frame status Error");
        }
    }
}
