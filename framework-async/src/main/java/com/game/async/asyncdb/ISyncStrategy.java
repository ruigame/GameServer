package com.game.async.asyncdb;

/**
 * @author liguorui
 * @date 2018/1/21 20:41
 */
public interface ISyncStrategy {

    /**
     * 每轮循环休眠多久
     * @param waitingSize
     * @return
     */
    long getSleepTime(int waitingSize);

    /**
     * 每轮循环同步多少个
     * @return
     */
    int getNumEachLoop();

    /**
     * 同步失败的重试次数
     * @return
     */
    int getTryTime();
}
