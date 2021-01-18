package com.game.net.packet;

import com.game.net.CrossBaseFrameDecoder;

/**
 * @Author: liguorui
 * @Date: 2020/12/1 下午10:54
 */
public abstract class CrossPacket extends AbstractPacket{

    private long packetPlayerId;

    public CrossPacket() {
        super();
    }

    public CrossPacket(long playerId) {
        super();
        this.packetPlayerId = playerId;
    }

    @Override
    protected void beforeRead(Request request) {
        this.packetPlayerId = request.readLong();
    }

    @Override
    protected void doResponse(Response response) {
        response.writeLong(packetPlayerId);
        doWriteCross(response);
    }

    protected abstract void doWriteCross(Response response);

    @Override
    public boolean isCross() {
        return true;
    }

    public long getPacketPlayerId() {
        return packetPlayerId;
    }

    public void setPacketPlayerId(long packetPlayerId) {
        this.packetPlayerId = packetPlayerId;
    }

    public boolean isPlayerPacket() {
        return this.packetPlayerId > 0;
    }

    @Override
    protected Response createResponse() {
        return ResponeFactory.createResponse(getCmd(), CrossBaseFrameDecoder.maxBodySize);
    }
}
