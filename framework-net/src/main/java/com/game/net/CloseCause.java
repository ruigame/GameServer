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
}
