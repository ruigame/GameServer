package com.game.util;

/**
 * 登陆参数
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
     * 区服名字
     */
    private String server;

    /**
     * 区服id
     */
    private int serverId;

    /**
     * 前端传递参数
     */
    private String param;
    /**
     * gameId，用于区分当前是那款游戏
     */
    private String gid;
    /**
     * 平台id
     */
    private String pid;
    /**
     * 秒级时间戳
     */
    private int time;
    /**
     * 签名
     */
    private String sign;
    /**
     * token
     */
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

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
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
