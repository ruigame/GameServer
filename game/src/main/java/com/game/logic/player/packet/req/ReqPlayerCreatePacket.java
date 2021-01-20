package com.game.logic.player.packet.req;

import com.game.PacketId;
import com.game.net.packet.Packet;
import com.game.net.packet.Request;
import com.game.net.packet.RequestBeforeLoginPacket;

/**
 * 请求创建角色
 * @Author: liguorui
 * @Date: 2021/1/20 下午11:15
 */
@Packet(commandId = PacketId.Base.REQ_PLAYER_CREATE)
public class ReqPlayerCreatePacket extends RequestBeforeLoginPacket {

    /**
     * 名称
     */
    private String nickName;
    /**
     * 角色类型
     */
    private byte roleType;

    /**
     * 性别
     */
    private byte gender;

    @Override
    public void read(Request request) throws Exception {
        gender = request.readByte();
        roleType = request.readByte();
        nickName = request.readString();
    }

    public String getNickName() {
        return nickName;
    }

    public byte getRoleType() {
        return roleType;
    }

    public byte getGender() {
        return gender;
    }
}
