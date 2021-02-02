package com.game.logic.client.tcp.net;

import com.game.net.packet.ResponsePacket;
import com.google.common.collect.Lists;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 消息消费者处理器
 * @Author: liguorui
 * @Date: 2021/1/28 下午9:41
 */
public class TcpClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 消息消费者列表
     */
    private List<Consumer<ResponsePacket>> consumers = Collections.emptyList();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ResponsePacket)) {
            return;
        }
        for (Consumer<ResponsePacket> consumer : consumers) {
            try {
                consumer.accept((ResponsePacket)msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addConsumer(Consumer<ResponsePacket> consumer) {
        List<Consumer<ResponsePacket>> consumers = Lists.newArrayList(this.consumers);
        consumers.add(consumer);
        this.consumers = consumers;
    }
}
