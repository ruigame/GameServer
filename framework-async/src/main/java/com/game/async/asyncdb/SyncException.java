package com.game.async.asyncdb;

/**
 * @author liguorui
 * @date 2018/2/11 01:08
 */
public class SyncException extends RuntimeException {

    private static final long serialVersionUID = 8969047828956839026L;

    public SyncException(String message, Throwable cause) {
        super(message, cause);
    }
}
