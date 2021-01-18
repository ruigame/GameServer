package com.game.net.packet;

/**
 * @Author: liguorui
 * @Date: 2020/12/1 下午10:47
 */
public abstract class AdminPacket extends AbstractPacket {

    private int time;
    private String key;

    @Override
    protected void beforeRead(Request request) {
        this.time = request.readInt();
        this.key = request.readString();
    }

    @Override
    protected void doResponse(Response response) {

    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    public String getKey() {
        return key;
    }

    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + cmd + "]";
    }
}
