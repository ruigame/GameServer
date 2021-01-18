package com.game.async.asynchttp.example;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 5:09 下午
 */
public class Player {

    private long playerId;

    private String name;

    /**
     * 账号
     */
    private String account;

    /**
     * 服务器id
     */
    private int serverId;

    /**
     * 创建时间
     */
    private int createTime;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }
}
