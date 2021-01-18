package com.game.net;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 8:47 下午
 */
public class LoginAuthParam {

    /**
     * 账号
     */
    private String account;

    /**
     * 平台
     */
    private String platform;

    /**
     * 区服id
     */
    private int server;

    /**
     * 参数
     */
    private String param;

    private String gid;
    private String pid;
    private String time;
    private String sign;
    private String token;

    @Override
    public String toString() {
        return "LoginAuthParam{" +
                "account='" + account + '\'' +
                ", platform='" + platform + '\'' +
                ", server=" + server +
                ", param='" + param + '\'' +
                ", gid='" + gid + '\'' +
                ", pid='" + pid + '\'' +
                ", time='" + time + '\'' +
                ", sign='" + sign + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
