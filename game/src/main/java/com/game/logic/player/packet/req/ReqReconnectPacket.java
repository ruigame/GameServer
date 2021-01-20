package com.game.logic.player.packet.req;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Request;
import com.game.net.packet.RequestBeforeLoginPacket;

/**
 * @Author: liguorui
 * @Date: 2021/1/20 下午11:32
 */
@Packet(commandId = PacketId.Base.REQ_RECONNECT)
public class ReqReconnectPacket extends RequestBeforeLoginPacket {

    /**
     * 平台账号
     */
    private String account;
    /**
     * 服务器ID
     */
    private int serverId;
    /**
     * 平台
     */
    private String platform;
    /**
     * 链接key
     */
    private String connectKey;
    /**
     * 最后响应包序列号
     */
    private int respSN;

    @Override
    public void read(Request request) throws Exception {
        this.platform = request.readString();
        this.serverId = request.readInt();
        this.account = request.readString();
        this.connectKey = request.readString();
        this.respSN = request.readInt();
    }

    public String getAccount() {
        return account;
    }

    public int getServerId() {
        return serverId;
    }

    public String getPlatform() {
        return platform;
    }

    public String getConnectKey() {
        return connectKey;
    }

    public int getRespSN() {
        return respSN;
    }
}
