package com.game.net;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 8:55 下午
 */
public enum CloseCause {

    /**
     * 服务器维护
     */
    MAINTAIN,
    /**
     * 重登陆
     */
    Duplicate_login,
    /**
     * 封号
     */
    FORBID_ROLE,
    /**
     * 空闲超时
     */
    IDLE_TIMEOUT,
    /**
     * 包太多
     */
    PACKET_TOO_MANY,
    /**
     * 客户端网络错误
     */
    CLIENT_NET_ERROR,
    /**
     * 封号
     */
    BAN,
    /**
     * 不是后台白名单
     */
    ADMIN_NOT_WHITE,
    /**
     * 后台包验证错误
     */
    ADMIN_KEY_INVALID,
    ;

    /**
     * 是否可以重新登陆
     */
    private final boolean relogin;

    private CloseCause() {
        this(false);
    }

    private CloseCause(boolean relogin) {
        this.relogin = relogin;
    }

    public boolean isRelogin() {
        return relogin;
    }
}
