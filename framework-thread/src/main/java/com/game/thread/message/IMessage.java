package com.game.thread.message;

/**
 * @author liguorui
 * @date 2018/1/7 18:25
 */
public interface IMessage<H extends IMessageHandler<?>> {
    void execute(H h);

    default String name() {
        return getClass().toString();
    }
}
