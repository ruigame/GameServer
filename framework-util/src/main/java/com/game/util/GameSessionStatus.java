package com.game.util;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 8:43 下午
 */
public enum GameSessionStatus {

    /**
     * 初始
     */
    INIT,
    /**
     * 验证中
     */
    AUTHING,
    /**
     * 已验证
     */
    AUTHED,
    /**
     * 已注册在线
     */
    REGISTER,
    /**
     * 正在进入场景（还没初始化玩家数据）
     */
    ENTERING_SCENE,
    /**
     * 已进入场景（已经初始化玩家数据）
     */
    ENTERD_SCENE,
    /**
     * 退出中
     */
    LOGOUTING,
    ;


}
