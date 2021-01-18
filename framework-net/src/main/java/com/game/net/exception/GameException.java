package com.game.net.exception;

/**
 * @Author: liguorui
 * @Date: 2020/12/1 上午12:57
 */
public class GameException extends RuntimeException{

    private static final long serialVersionUID = 356921953125390518L;

    public GameException() {
        super();
    }

    public GameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameException(String message) {
        super(message);
    }

    public GameException(Throwable cause) {
        super(cause);
    }
}
