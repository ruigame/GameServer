package com.game.logic.admin.packet.req;

import com.game.PacketId;
import com.game.net.packet.AdminPacket;
import com.game.net.packet.Packet;
import com.game.net.packet.Request;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: liguorui
 * @Date: 2021/1/28 上午12:39
 */
@Packet(commandId = PacketId.Admin.REQ_ADMIN_KICK_BY_ACCOUNT)
public class ReqAdminKickByIdPacket extends AdminPacket {

    private Set<String> accounts;

    @Override
    public void read(Request request) throws Exception {
        Set<String> accounts = new HashSet<>();
        int playerNum = request.readInt();
        for (int i = 0; i < playerNum; i++) {
            String account = request.readString();
            accounts.add(account);
        }
        this.accounts = accounts;
    }

    public Set<String> getAccounts() {
        return accounts;
    }
}
