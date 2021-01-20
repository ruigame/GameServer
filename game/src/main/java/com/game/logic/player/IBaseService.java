package com.game.logic.player;

import com.game.logic.player.packet.req.*;
import com.game.net.packet.PacketHandler;
import com.game.util.GameSession;

/**
 * @Author: liguorui
 * @Date: 2021/1/20 下午11:14
 */
@PacketHandler
public interface IBaseService {

    /**
     * 登陆验证
     * @param session
     * @param packet
     */
    void loginAuth(GameSession session, ReqLoginAuthPacket packet);

    /**
     * 请求随机名
     * @param session
     * @param packet
     */
    void randomName(GameSession session, ReqNameRandomPacket packet);

    /**
     * 请求创建角色
     * @param session
     * @param packet
     */
    void roleCreate(GameSession session, ReqPlayerCreatePacket packet);

    /**
     * 请求登陆，发送消息
     * @param session
     * @param packet
     */
    void loginAsk(GameSession session, ReqLoginAskPacket packet);

    /**
     * 请求重连
     * @param session
     * @param packet
     */
    void reconnect(GameSession session, ReqReconnectPacket packet);

    /**
     * 请求系统心跳
     * @param session
     * @param packet
     */
    void systemHeartbeat(GameSession session, ReqSystemHeartbeatPacket packet);

    /**
     * 请求随机名(不分性别，仅供展示)
     * @param session
     * @param packet
     */
    void reqRandomNamePacket(GameSession session, ReqRandomNameShowPacket packet);
}
