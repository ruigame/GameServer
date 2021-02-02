package com.game.framework.handler;

import com.game.logic.common.PlayerActor;
import com.game.net.packet.AbstractPacket;
import com.game.net.packet.PacketHandlerWrapper;
import com.game.thread.message.IMessage;
import com.game.util.RootLogUtils;
import com.game.util.UseTimer;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午4:59
 */
public class PacketMessage implements IMessage<PlayerActor> {

    private final PacketHandlerWrapper wrapper;
    private final PlayerActor player;
    private final AbstractPacket packet;

    public PacketMessage(PacketHandlerWrapper wrapper, PlayerActor player, AbstractPacket packet) {
        this.wrapper = wrapper;
        this.player = player;
        this.packet = packet;
    }

    public PacketHandlerWrapper getWrapper() {
        return wrapper;
    }

    public PlayerActor getPlayer() {
        return player;
    }

    public AbstractPacket getPacket() {
        return packet;
    }

    @Override
    public void execute(PlayerActor player) {
        try {
            if (!player.isLogined()) {
                RootLogUtils.infoLog("玩家已下线，不处理残留协议。account:{},packet:{}", player.getAccount(), packet.getCmd());
                return;
            }
            if (player.isDiscardReqPacet()) {
                RootLogUtils.infoLog("玩家丢弃请求包，不处理残留协议。account:{},packet:{}", player.getAccount(), packet.getCmd());
                return;
            }
            UseTimer useTimer = new UseTimer(player.getAccount() + " PacketMessage handlePlayerPacket " + packet.getCmd(), 1000);
            wrapper.invoke(player, packet);
            useTimer.printUseTime();
        } catch (Exception e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
//            if (cause != null) {
//                ExceptionUtils.log("account:{}, packet:{}, [{}]", player.getAccount(), packet.getCmd(), ClassUtils.printlnallfield(packet), cause);
//            } else {
//                ExceptionUtils.log("account:{}, packet:{}, [{}]", player.getAccount(), packet.getCmd(), ClassUtils.printlnallfield(packet), e);
//            }
        }
    }
    @Override
    public String name() {
        return packet.toString();
    }
}
