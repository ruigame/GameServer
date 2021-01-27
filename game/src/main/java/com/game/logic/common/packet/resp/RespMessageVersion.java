package com.game.logic.common.packet.resp;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.PacketFactory;
import com.game.net.packet.ResponsePacket;

/**
 * @Author: liguorui
 * @Date: 2021/1/28 上午12:30
 */
@Packet(commandId = PacketId.Base.RESP_VERSION)
public class RespMessageVersion extends ResponsePacket {
    /**
     * 服务器当前协议版本号
     */
    private String version;

    public static RespMessageVersion create(String version) {
        RespMessageVersion packet = PacketFactory.createPacket(PacketId.Base.RESP_VERSION);
        packet.version = version;
        return packet;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
