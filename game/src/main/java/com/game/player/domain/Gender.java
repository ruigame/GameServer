package com.game.player.domain;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 8:26 下午
 */
public enum Gender {

    NONE(0, "无"),
    MALE(1, "男"),
    FEMALE(1, "女"),
    ;

    private int id;

    private String desc;

    public static Gender[] genders = values();

    Gender(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static Gender getById(int id) {
        for (Gender gender : genders) {
            if (gender.getId() == id) {
                return gender;
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
