package com.game.logic.player.domain;

import com.game.util.Unique;

/**
 * @Author: liguorui
 * @Date: 2021/1/18 下午11:04
 */
public enum OperationType implements Unique {
    /**玩家模块10001**/
    LOGIN_INIT(10001, "玩家模块", "登陆初始化"),
    /**物品11001**/
    USE_GOODS(11001, "物品模块", "使用{0} * {1},获得了{2}"),
    /**GM系统12001**/
    GM_COMMAND(12001, "GM命令", "GM命令"),
    GM_ADD_GOODS(12002, "GM命令", "GM指令添加物品"),
    /**后台系统13001**/
    SYS_SEND(13001, "后台", "后台发放元宝"),
    SYS_ADD_GOODS(13002, "后台", "后台添加物品"),
    ;

    private int id;
    private String name;
    private String desc;

    OperationType(int id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public static final OperationType[] elements = values();

    public static OperationType getById(int id) {
        for (OperationType operationType : elements) {
            if (operationType.getId() == id) {
                return operationType;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public Object uniqueID() {
        return getId();
    }
}
