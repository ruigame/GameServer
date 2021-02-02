package com.game.framework.handler;

import com.game.net.packet.*;
import com.game.util.GameSession;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午4:45
 */
@Component
public class CrossPacketHandler implements IPacketHandler {
    @Override
    public PacketType getPacketType() {
        return PacketType.CROSS;
    }

    @Override
    public void handlePacket(GameSession session, AbstractPacket packet, PacketHandlerWrapper wrapper) {
        Preconditions.checkNotNull(wrapper, "NoCrossPacket for %s", packet.getClass().getSimpleName());
        if (!(packet instanceof CrossPacket)) return;
        CrossPacket crossPacket = (CrossPacket)packet;
        if (!crossPacket.isPlayerPacket()) {
//            Context.getBean(Crossservice.class).execute(wrapper, packet);
            return;
        }
        long playerId = crossPacket.getPacketPlayerId();
    }
}
