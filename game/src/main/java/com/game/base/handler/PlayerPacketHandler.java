package com.game.base.handler;

import com.game.base.PlayerActor;
import com.game.net.packet.AbstractPacket;
import com.game.net.packet.IPacketHandler;
import com.game.net.packet.PacketHandlerWrapper;
import com.game.net.packet.PacketType;
import com.game.util.GameSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午4:58
 */
@Component
public class PlayerPacketHandler implements IPacketHandler {

    @Autowired
    private PacketMonitorService packetMonitorService;
//
//    @Autowired
//    private Onlineservice onlineservice;

    @Override
    public PacketType getPacketType() {
        return PacketType.PLAYER;
    }

    @Override
    public void handlePacket(GameSession session, AbstractPacket packet, PacketHandlerWrapper wrapper) {
        long playerId = session.getPlayerId();
//        PlayerActor playerActor = onlineservice.getOnlinePlayer(playerId);
//        if (playerActor == null) return;
//        playerMonitorService.incPacket(playerActor, packet);
//        playerActor.addMessage(new PacketMessage(wrapper, playerId, packet));
    }
}
