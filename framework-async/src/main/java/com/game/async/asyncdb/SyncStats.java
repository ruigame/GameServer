package com.game.async.asyncdb;

/**
 * @author liguorui
 * @date 2018/1/21 21:06
 */
public class SyncStats {

    private int waiting;

    private long total;

    private long periodNum;

    public SyncStats(int waiting, long total, long periodNum) {
        super();
        this.waiting = waiting;
        this.total =  total;
        this.periodNum = periodNum;
    }

    public int getWaiting() {
        return waiting;
    }

    public long getTotal() {
        return total;
    }

    public long getPeriodNum() {
        return periodNum;
    }
}
