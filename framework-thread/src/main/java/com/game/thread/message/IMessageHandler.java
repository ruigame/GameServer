package com.game.thread.message;

/**
 * @author liguorui
 * @date 2018/1/7 18:26
 */
public interface IMessageHandler<H extends IMessageHandler<?>> {

    void addMessage(IMessage<H> msg);
}
