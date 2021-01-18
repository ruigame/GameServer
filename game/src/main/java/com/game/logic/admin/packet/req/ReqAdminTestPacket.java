package com.game.logic.admin.packet.req;

import com.game.PacketId;
import com.game.net.packet.AdminPacket;
import com.game.net.packet.Packet;
import com.game.net.packet.Request;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午3:06
 */
@Packet(commandId = PacketId.Admin.REQ_ADMIN_TEST)
public class ReqAdminTestPacket extends AdminPacket {

    private String test;

    @Override
    public void read(Request request) throws Exception {
        this.test = request.readString();
    }

    public String getTest() {
        return test;
    }
}
