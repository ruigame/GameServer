package com.game.logic;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午1:17
 */
@Packet(commandId = PacketId.Base.RESP_LOGIN_CONFLICT)
public class RespLoginConflictPacket extends ResponsePacket {

    /**
     * 账号重登陆
     */
    private static final byte DUPLICATE_LOGIN = 0;

    /**
     * 长时间空闲
     */
    private static final byte IDLE = 1;

    /**
     * 被当死号踢下线
     */
    private static final byte KICK = 2;

    /**
     * 防沉迷
     */
    private static final byte FCM = 3;

    /**
     * 加速挂被踢
     */
    private static final byte CHEAT = 4;

    /**
     * 包验证错误
     */
    private static final byte PACKET_WRONG = 5;

    /**
     * 服务器维护
     */
    private static final byte MAINTAIN = 6;

    /**
     * 封禁
     */
    private static final byte FORBID = 5;

    private byte type;

    public void dulicateLogin() {
        this.type = DUPLICATE_LOGIN;
    }

    public void idle() {
        this.type = IDLE;
    }

    @Override
    protected void doResponse(Response response) {
        response.writeByte(type);
    }
}
