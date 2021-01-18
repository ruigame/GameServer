package com.game.logic.player.domain;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 8:23 下午
 */
public enum RoleType {

    None(0, "无"),
    ZHAN_SHI(1, "战士"),
    FA_SHI(2, "法师"),
    DAO_SHI(3, "道士"),
    ;

    private int id;

    private String desc;

    public static RoleType[] roles = values();

    RoleType(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static RoleType getById(int id) {
        for (RoleType roleType : roles) {
            if (roleType.getId() == id) {
                return roleType;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
