package com.game.framework.handler;

import com.game.thread.gate.GateKeeper;
import com.game.thread.message.IMessage;
import com.game.util.GameSession;
import com.game.net.packet.AbstractPacket;
import com.game.net.packet.IPacketHandler;
import com.game.net.packet.PacketHandlerWrapper;
import com.game.net.packet.PacketType;
import com.game.util.UseTimer;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午4:22
 */
@Component
public class BeforeLoginPacketHandler implements IPacketHandler {
    @Override
    public PacketType getPacketType() {
        return PacketType.BEFORELOGIN;
    }

    @Override
    public void handlePacket(GameSession session, AbstractPacket packet, PacketHandlerWrapper wrapper) {
        GateKeeper.executeMessage(session, new IMessage<GateKeeper>() {
            @Override
            public void execute(GateKeeper gateKeeper) {
                try {
                    UseTimer useTimer = new UseTimer("handleSessionPacket" + packet.getCmd(), 1000);
                    wrapper.invoke(session, packet);
                    useTimer.printUseTime();
                } catch (Exception e) {
                    e.printStackTrace();
                    Throwable cause = e.getCause();
//                    if (cause != null) {
//                        ExceptionUtils.log("account:{}, packet:{}, [{}]", session.getAccount(), packet.getCmd(), ClassUtils.printlnallfield(packet), cause);
//                    } else {
//                        ExceptionUtils.log("account:{}, packet:{}, [{}]", session.getAccount(), packet.getCmd(), ClassUtils.printlnallfield(packet), e);
//                    }
                }
            }
            @Override
            public String name() {
                return packet.toString();
            }
        });
    }
}
