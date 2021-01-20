package com.game.logic.desc.domain;

/**
 * 消息类型
 * @Author: liguorui
 * @Date: 2021/1/20 下午9:13
 */
public enum MessageType {

    /**
     * 系统成功，在正下方位置
     */
    SUCCEED(1, "操作成功提示"),
    /**
     * 系统警告，在正下方位置
     */
    WARN(2, "警告"),
    /**
     * 系统错误，在正下方
     */
    ERROR(3, "错误"),
    /**
     * 全服公告，走马灯
     */
    ANNOUNCEMENT(4, "全服公告"),
    /**
     * 显示在聊天框系统频道
     */
    SYSCHAT(5, "显示在聊天框系统频道"),
    /**
     * 公会聊天频道
     */
    GUILD(6, "公会聊天频道"),
    /**
     * 跨服聊天频道
     */
    CROSS(7, "跨服聊天频道"),
    ;

    private int code;
    private String desc;

    MessageType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
