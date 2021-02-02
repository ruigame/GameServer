package com.game.net.cross;

import com.game.util.ChannelUtils;
import com.game.net.packet.ByteBufResponse;
import com.game.net.packet.PacketFactory;
import com.game.util.GameSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: liguorui
 * @Date: 2020/12/14 下午9:46
 */
@ChannelHandler.Sharable
public class CrossResponseEncoder extends MessageToByteEncoder<ByteBufResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrossResponseEncoder.class);

    private short[] excludeIds;

    public CrossResponseEncoder(short[]excludeIds) {
        this.excludeIds = excludeIds;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx,
                          ByteBufResponse response, ByteBuf out) throws Exception {
        ByteBuf byteBuf = response.getByteBuf();
        short cmdId = response.getPacketId();
        /**
         * 排除某些不想看到的packet
         */
        if (LOGGER.isDebugEnabled()) {
            if (!ArrayUtils.contains(excludeIds, cmdId)) {
                log(ctx.channel(), byteBuf, cmdId);
            }
        }
        out.writeInt(byteBuf.readableBytes());
        out.writeBytes(byteBuf, 0, byteBuf.readableBytes());
    }

    private void log(Channel channel, ByteBuf byteBuf, short cmdId) {
        GameSession session  = ChannelUtils.getChannelSession(channel);
        Class<?> packetClass = PacketFactory.getPacketClassByCommonId(cmdId);
        String commandStr = String.valueOf(packetClass != null ? packetClass.getSimpleName() : cmdId);
        LOGGER.debug("cross Send : {}, {}, {}, {}, {}", cmdId, session.getRespSN(), commandStr, session.getAccount(), channel);
    }
}
