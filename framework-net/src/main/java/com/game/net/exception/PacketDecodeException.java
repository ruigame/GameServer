package com.game.net.exception;

/**
 * @Author: liguorui
 * @Date: 2020/12/1 上午12:56
 */
public class PacketDecodeException extends GameException {

    private static final long serialVersionUID = 1L;

    public PacketDecodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
