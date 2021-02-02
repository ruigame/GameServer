package com.game.logic.player;

import com.game.logic.common.PlayerActor;
import com.game.logic.player.packet.resp.RespForbidedMessagePacket;
import com.game.net.packet.PacketHandler;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:36
 */
@PacketHandler
public interface IForbidService {

    void forbidRole(PlayerActor playerActor, int limitTime, String reason);
    void clearForbidRole(PlayerActor playerActor);
    boolean isRoleForbided(PlayerActor playerActor);
    RespForbidedMessagePacket createRoleForbidedPacket(PlayerActor playerActor);
    RespForbidedMessagePacket createRoleForbidedIPPacket(PlayerActor playerActor, String ip);
    void mutePlayer(long playerId, int muteTime, String reason);
    boolean isMute(long playerId);
    void banIps(String ips, int second, String reason);
    void clearBanIps(String ips);
    boolean isIPBan(String ip);

}
