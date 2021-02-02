package com.game.net;

import com.game.net.packet.ByteBufResponse;
import com.game.net.packet.PacketFactory;
import com.game.util.ChannelUtils;
import com.game.util.GameSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liguorui
 * @date 2018/1/14 15:32
 */
@ChannelHandler.Sharable
public class ResponseEncoder extends MessageToByteEncoder<ByteBufResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseEncoder.class);

    private boolean EXCLUDE = true;

    private GameStartParams gameStartParams;

    public ResponseEncoder(GameStartParams gameStartParams) {
        this.gameStartParams = gameStartParams;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBufResponse resposne, ByteBuf out) throws Exception {
        ByteBuf byteBuf = resposne.getByteBuf();
        short command = resposne.getPacketId();
        int respSN = incRespSN(ctx.channel());
        /**
         * 排除某些不想看到的packet
         */
        if(LOGGER.isDebugEnabled()) {
            if (!EXCLUDE || !ArrayUtils.contains(gameStartParams.getResponseEncoderExcludeIds(), command)) {
                log(ctx.channel(), byteBuf, command);
            }
        }
        out.writeInt(byteBuf.readableBytes() + 4);
        out.writeInt(respSN);
        out.writeBytes(byteBuf, 0 ,byteBuf.readableBytes());
    }

    private void log(Channel channel, ByteBuf byteBuf, short command) {
        GameSession session = ChannelUtils.getChannelSession(channel);
        Class<?> packetClass = PacketFactory.getPacketClassByCommonId(command);
        String commandStr = String.valueOf(packetClass != null ? packetClass.getSimpleName() : command);
        LOGGER.debug("Send {}, {}, {}, {}, {}", command, session.getRespSN(), commandStr, session.getAccount(), channel);
    }

    private int incRespSN(Channel channel) {
        GameSession session = ChannelUtils.getChannelSession(channel);
        return session.inRespSN();
    }
}



