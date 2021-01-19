package com.game.logic.player.packet.resp;

import com.game.PacketId;
import com.game.logic.player.domain.RoleInfoVo;
import com.game.net.packet.Packet;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;

/**
 * 返回开启新角色结果
 * @Author: liguorui
 * @Date: 2021/1/19 下午11:29
 */
@Packet(commandId = PacketId.Role.RESP_ROLE_CREATE)
public class RespRoleCreatePacket extends ResponsePacket {

    private RoleInfoVo roleInfoVo;

    public void init(RoleInfoVo roleInfoVo) {
        this.roleInfoVo = roleInfoVo;
    }

    @Override
    protected void doResponse(Response response) {
        roleInfoVo.write(response);
    }
}
