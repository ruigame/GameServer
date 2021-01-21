package com.game.logic.player.domain.forbid;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:10
 */
public class ForbidIpDataKey implements Serializable {

    private static final long serialVersionUID = 4383055183955053179L;

    private String ip;
    private int serverId;

    public ForbidIpDataKey() {
        super();
    }

    public ForbidIpDataKey(String ip, int serverId) {
        super();
        this.ip = ip;
        this.serverId = serverId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForbidIpDataKey that = (ForbidIpDataKey) o;
        return serverId == that.serverId &&
                ip.equals(that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, serverId);
    }
}
