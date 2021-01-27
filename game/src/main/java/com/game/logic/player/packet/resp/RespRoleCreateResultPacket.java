package com.game.logic.player.packet.resp;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;

/**
 * @Author: liguorui
 * @Date: 2021/1/25 下午10:08
 */
@Packet(commandId = PacketId.Base.RESP_CREATE_PLAYER)
public class RespRoleCreateResultPacket extends ResponsePacket {

    private static final byte FAILED = 0;  //失败
    private static final byte SUCC = 1;  //成功
    private static final byte EXIST_NAME = 2;  //存在这个用户名
    private static final byte FAILD_UNVALID_NAME_FORMAT = 3;  //非法的名字格式
    private static final byte INVALID_ROLETYPE = 4;  //无效职业
    private static final byte INVALID_OVERFLOW = 5;  //玩家id超过上限
    private static final byte INVALID_SERVER_NOT_HOLDED = 6;  //服务器不存在
    private static final byte PLAYER_CREATE_LIMIT = 7;  //超过创角色限制
    private static final byte NOT_GENDER = 8;  //没有该性别

    private int result = 0;

    public void succ() {
        result = SUCC;
    }

    public void fail() {
        result = FAILED;
    }

    public void fail4NameExists() {
        result = EXIST_NAME;
    }

    public void fail4NameFormat() {
        result = FAILD_UNVALID_NAME_FORMAT;
    }

    public void fail4NotRoleType() {
        result = INVALID_ROLETYPE;
    }

    public void fail4NotGender() {
        result = NOT_GENDER;
    }

    public void fail4InvalidRoleType() {
        result = INVALID_ROLETYPE;
    }

    public void fail4OverFlow() {
        result = INVALID_OVERFLOW;
    }

    public void fail4NotServer() {
        result = INVALID_SERVER_NOT_HOLDED;
    }

    public void fail4PlayerCreateLimit() {
        result = PLAYER_CREATE_LIMIT;
    }

    @Override
    protected void doResponse(Response response) {
        response.writeByte(result);
    }
}
