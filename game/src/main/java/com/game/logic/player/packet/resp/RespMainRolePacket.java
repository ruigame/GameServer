package com.game.logic.player.packet.resp;

import com.game.PacketId;
import com.game.logic.player.domain.PlayerInfoVo;
import com.game.net.packet.Packet;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;

/**
 * 主角信息
 * @Author: liguorui
 * @Date: 2021/1/18 下午10:15
 */
@Packet(commandId = PacketId.Role.RESP_MAIN_ROLE)
public class RespMainRolePacket extends ResponsePacket {

    private PlayerInfoVo playerInfoVo;

    public void initBaseInfo(PlayerInfoVo playerInfoVo) {
        this.playerInfoVo = playerInfoVo;
    }

    @Override
    protected void doResponse(Response response) {
        playerInfoVo.write(response);
    }
}
