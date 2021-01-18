package com.game.base.exception;

/**
 * @Author: liguorui
 * @Date: 2021/1/14 下午10:09
 */
public class PlayerIdOverflowException extends RuntimeException {

    private static final long serialVersionUID = -1;

    private long overflowId;

    public PlayerIdOverflowException(long overflowId) {
        super("ID Overflow " + overflowId);
        this.overflowId = overflowId;
    }

    public long getOverflowId() {
        return overflowId;
    }
}
