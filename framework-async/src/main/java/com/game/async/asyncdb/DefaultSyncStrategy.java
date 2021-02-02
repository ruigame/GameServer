package com.game.async.asyncdb;

import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2020/9/19 6:51 下午
 */
@Component
public class DefaultSyncStrategy implements ISyncStrategy {

    private int numEachLoop = 10;

    private int tryTime = 1;

    private int sleepTimeUpper50 = 10;

    private int sleepTime = 500;

    @Override
    public long getSleepTime(int waitingSize) {
        if (waitingSize > 50) {
            return sleepTimeUpper50;
        }
        return sleepTime;
    }

    @Override
    public int getNumEachLoop() {
        return numEachLoop;
    }

    @Override
    public int getTryTime() {
        return tryTime;
    }

    public void setNumEachLoop(int numEachLoop) {
        this.numEachLoop = numEachLoop;
    }

    public void setTryTime(int tryTime) {
        this.tryTime = tryTime;
    }

    public void setSleepTimeUpper50(int sleepTimeUpper50) {
        this.sleepTimeUpper50 = sleepTimeUpper50;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }
}
