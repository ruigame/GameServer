package com.game.logic.admin;

import com.game.logic.admin.packet.req.ReqAdminKickByIdPacket;
import com.game.logic.admin.packet.req.ReqAdminTestPacket;
import com.game.net.packet.PacketHandler;
import com.game.util.GameSession;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午2:39
 */
@PacketHandler
public interface IAdminService {

    void reqAdminTest(GameSession session, ReqAdminTestPacket req) ;

    void reqAdminKickPlayerByAccount(GameSession session, ReqAdminKickByIdPacket req);
}
