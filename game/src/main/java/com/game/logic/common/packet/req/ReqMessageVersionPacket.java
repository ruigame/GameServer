package com.game.logic.common.packet.req;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.RequestPacket;

/**
 * @Author: liguorui
 * @Date: 2021/1/28 上午12:23
 */
@Packet(commandId = PacketId.Base.REQ_VERSION)
public class ReqMessageVersionPacket extends RequestPacket {
}
