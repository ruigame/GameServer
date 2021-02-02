package com.game.logic.player.packet.req;

import com.game.PacketId;
import com.game.util.LoginAuthParam;
import com.game.net.packet.Packet;
import com.game.net.packet.Request;
import com.game.net.packet.RequestBeforeLoginPacket;

/**
 * 请求验证登陆
 * @Author: liguorui
 * @Date: 2021/1/20 下午11:15
 */
@Packet(commandId = PacketId.Base.REQ_LOGIN_AUTH)
public class ReqLoginAuthPacket extends RequestBeforeLoginPacket {

    private LoginAuthParam loginAuthParam;

    public void read(Request request) {
        String platform = request.readString();
        int serverId = request.readInt();
        String account = request.readString();
        String server = request.readString();
        String gid = request.readString();
        String pid = request.readString();
        int time = request.readInt();
        String sign = request.readString();
        String token = request.readString();
        String param = request.readString();

        LoginAuthParam loginAuthParam = new LoginAuthParam();
        loginAuthParam.setPlatform(platform);
        loginAuthParam.setAccount(account);
        loginAuthParam.setServer(server);
        loginAuthParam.setServerId(serverId);
        loginAuthParam.setGid(gid);
        loginAuthParam.setPid(pid);
        loginAuthParam.setTime(time);
        loginAuthParam.setSign(sign);
        loginAuthParam.setToken(token);
        loginAuthParam.setParam(param);
        this.loginAuthParam = loginAuthParam;
    }

    public LoginAuthParam getLoginAuthParam() {
        return loginAuthParam;
    }

    public void setLoginAuthParam(LoginAuthParam loginAuthParam) {
        this.loginAuthParam = loginAuthParam;
    }
}
