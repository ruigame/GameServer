package com.game.net.packet;

/**
 * @Author: liguorui
 * @Date: 2020/12/1 下午10:36
 */
public enum PacketType {

    /**
     * 后台包
     */
    ADMIN,
    /**
     * 玩家包
     */
    PLAYER,
    /**
     * 跨服包
     */
    CROSS,
    /**
     * 登陆前请求包
     */
    BEFORELOGIN,
    ;
}
