package com.game.logic.player.packet.resp;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.PacketFactory;
import com.game.net.packet.ResponsePacket;

/**
 * 返回通知前端模块加载完成
 * @Author: liguorui
 * @Date: 2021/1/28 下午10:25
 */
@Packet(commandId = PacketId.Base.RESP_MODULE_INIT_END)
public class RespModuleInitEndPacket extends ResponsePacket {

    public static RespModuleInitEndPacket create() {
        return PacketFactory.createPacket(PacketId.Base.RESP_MODULE_INIT_END);
    }
}
