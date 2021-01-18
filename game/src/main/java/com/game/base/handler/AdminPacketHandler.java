package com.game.base.handler;

import com.game.logic.admin.AdminService;
import com.game.util.GameSession;
import com.game.net.packet.*;
import com.game.util.Context;
import com.game.util.ExceptionUtils;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午4:18
 */
@Component
public class AdminPacketHandler implements IPacketHandler {
    @Override
    public PacketType getPacketType() {
        return PacketType.ADMIN;
    }

    @Override
    public void handlePacket(GameSession session, AbstractPacket packet, PacketHandlerWrapper wrapper) {
        Preconditions.checkNotNull(wrapper, "NoAdminPacketHandler for %s", packet.getClass().getSimpleName());
        if (packet instanceof AdminPacket) {
            Context.getBean(AdminService.class).execute(wrapper, session, packet);
        } else {
            ExceptionUtils.log("后台端口接收异常协议包：{}", packet.getCmd());
        }
    }
}
