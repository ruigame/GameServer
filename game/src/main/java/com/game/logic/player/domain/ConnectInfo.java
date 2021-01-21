package com.game.logic.player.domain;

import com.game.net.LoginAuthParam;

/**
 * @Author: liguorui
 * @Date: 2021/1/20 下午11:43
 */
public class ConnectInfo {

    private LoginAuthParam loginAuthParam;
    private String connectKey;
    private int respSN;

    public ConnectInfo(LoginAuthParam loginAuthParam, String connectKey, int respSN) {
        super();
        this.loginAuthParam = loginAuthParam;
        this.connectKey = connectKey;
        this.respSN = respSN;
    }

    public LoginAuthParam getLoginAuthParam() {
        return loginAuthParam;
    }

    public void setLoginAuthParam(LoginAuthParam loginAuthParam) {
        this.loginAuthParam = loginAuthParam;
    }

    public String getConnectKey() {
        return connectKey;
    }

    public void setConnectKey(String connectKey) {
        this.connectKey = connectKey;
    }

    public int getRespSN() {
        return respSN;
    }

    public void setRespSN(int respSN) {
        this.respSN = respSN;
    }
}
