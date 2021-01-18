package com.game.net.cross;

import com.game.file.prop.PropConfigStore;
import com.game.net.NetServerBuild;
import com.game.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2020/12/14 下午10:04
 */
public class CrossServerCheckerHandler extends ChannelInboundHandlerAdapter {

    public final static byte[] FRAME_MAGIC = {'c', 'r', 'o', 's', 's'};

    private final static Logger LOGGER = LoggerFactory.getLogger(CrossServerCheckerHandler.class);

    public static final int MAX_BODY_SIZE = 10240;
    private final static int FRAME_HEAD_BODY_SIZE = 4;
    private int frameStatus = 0;
    private int bodySize = 0;
    private final static int FRAME_STATUS_NEED_HEADER = 0;
    private final static int FRAME_STATUS_NEED_BODY = 1;
    private final static int FRAME_STATUS_NEED_CROSS_DOMAIN = 2;

    private ByteBuf cumulation;
    private ByteToMessageDecoder.Cumulator cumulator = ByteToMessageDecoder.MERGE_CUMULATOR;

    private NetServerBuild netServerBuild;

    public CrossServerCheckerHandler(NetServerBuild netServerBuild) {
        this.netServerBuild = netServerBuild;
    }

    public static boolean isMagic(byte[]magics) {
        return Arrays.equals(magics, FRAME_MAGIC);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf data = (ByteBuf)msg;
        if (cumulation == null) {
            cumulation = data;
        } else {
            cumulation = cumulator.cumulate(ctx.alloc(), cumulation, data);
        }

        ByteBuf in = cumulation;
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
                    throw new CorruptedFrameException(String.format("FrameMagicHeaderErroe: magics=%s", Arrays.toString(magics), hexDump));
                }
                bodySize = in.readInt();
                frameStatus  =FRAME_STATUS_NEED_BODY;
            case FRAME_STATUS_NEED_BODY:
                if (bodySize > MAX_BODY_SIZE) {
                    throw new RuntimeException(String.format("BodySize[%s] too large!", bodySize));
                }
                if (in.readableBytes() < bodySize) {
                    return;
                }

                ByteBuf frameBuf = ctx.alloc().heapBuffer(bodySize);
                frameBuf.writeBytes(in, bodySize);
                frameStatus = FRAME_STATUS_NEED_HEADER;

                //校验
                int time = frameBuf.readInt();
                String key = ByteBufUtils.readString(frameBuf);
                String crossKey = PropConfigStore.CROSS_CONFIG.getStr("CROSS_KEY"); //cross
                String rightKey = DigestUtils.md5Hex(time + crossKey);
                if (!rightKey.equals(key)) {
                    LOGGER.warn("CloseSession due to check error time {} key {} rightKey {}", time, key, rightKey);
                    ctx.channel().close();
                    return;
                }

                String platform = ByteBufUtils.readString(frameBuf);
                int serverId = frameBuf.readInt();
                int size = frameBuf.readShort();
                List<Integer> holdServerIdList = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    holdServerIdList.add(frameBuf.readInt());
                }
                frameBuf.release();
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.remove(this);
                ctx.fireChannelRead(in);
                afterChannelRead(platform, serverId, ctx.channel(), holdServerIdList);
            case FRAME_STATUS_NEED_CROSS_DOMAIN:
                in.clear();
                return;
            default:
                throw new Error("Decode Frame Status Error");
        }
    }

    public void afterChannelRead(String platform, int serverId, Channel curChannel, List<Integer> holdServerIdList) {
        netServerBuild.afterChannelRead(platform, serverId, curChannel, holdServerIdList);
    }
}
