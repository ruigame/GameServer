package com.game.logic.admin;

import com.game.logic.admin.packet.req.ReqAdminTestPacket;
import com.game.util.GameSession;
import com.game.net.packet.PacketHandler;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午2:39
 */
@PacketHandler
public interface IAdminService {

    void reqAdminTest(GameSession session, ReqAdminTestPacket req) ;
}
