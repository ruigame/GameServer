package com.game.logic.player.domain.forbid;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:25
 */
public class ForbidEntityKey implements Serializable {

    private static final long serialVersionUID = -8222019630381853816L;

    private String account;

    private String server;

    public ForbidEntityKey() {
        super();
    }

    public ForbidEntityKey(String account, String server) {
        super();
        this.account = account;
        this.server = server;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForbidEntityKey that = (ForbidEntityKey) o;
        return account.equals(that.account) &&
                server.equals(that.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, server);
    }
}
