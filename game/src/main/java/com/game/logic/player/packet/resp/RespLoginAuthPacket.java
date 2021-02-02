package com.game.logic.player.packet.resp;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;

/**
 * 返回登陆验证信息
 * @Author: liguorui
 * @Date: 2021/1/21 下午9:04
 */
@Packet(commandId = PacketId.Base.RESP_LOGIN_AUTH)
public class RespLoginAuthPacket extends ResponsePacket {

    /**
     * 正常成功
     */
    public static final byte STATUS_SUCCEED = 1;

    /**
     * 验证失败
     */
    public static final byte STATUS_FAIL = 2;

    /**
     * 被封号
     */
    public static final byte STATUS_BANNED = 3;

    /**
     * IP被封
     */
    public static final byte STATUS_BANNED_IP = 4;

    /**
     * 服务器关闭
     */
    public static final byte STATUS_SERVER_CLOSE = 5;

    /**
     * 跨服发送数据失败
     */
    public static final byte CROSS_SEND_INFO_FAIL = 6;

    /**
     * 同一IP登陆数量超出限制
     */
    public static final byte IP_COUNT_LIMIT = 7;

    private byte status;
    private boolean hasPlayer;

    public void success() {
        this.status = STATUS_SUCCEED;
    }

    public void fail() {
        this.status = STATUS_FAIL;
    }

    public void ban() {
        this.status = STATUS_BANNED;
    }

    public void banIP() {
        this.status = STATUS_BANNED_IP;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public void setHasPlayer(boolean hasPlayer) {
        this.hasPlayer = hasPlayer;
    }

    public boolean isHashPlayer() {
        return hasPlayer;
    }

    @Override
    protected void doResponse(Response response) {
        response.writeByte(status);
        response.writeBoolean(hasPlayer);
    }
}
