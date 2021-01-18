package com.game.base;

import com.game.net.packet.AbstractPacket;
import com.game.thread.message.IMessage;
import com.game.thread.message.IMessageHandler;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午5:00
 */
public interface IPlayer<H extends IMessageHandler<?>> extends PacketSender {

    String getName();

    long getPlayerId();

    void addMessage(IMessage<H> message);

    void sendPacket(AbstractPacket packet);

    void tryEnterScene();

    boolean isInThread();

}
