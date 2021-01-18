package com.game.base.exception;

/**
 * @Author: liguorui
 * @Date: 2021/1/14 下午10:09
 */
public class ServerNotHoldedException extends RuntimeException {

    private static final long serialVersionUID = -1;

    private int serverId;

    public ServerNotHoldedException(int serverId) {
        super("Server Not Holded " + serverId);
        this.serverId = serverId;
    }

    public int getServerId() {
        return serverId;
    }
}
