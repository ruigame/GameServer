package com.game.logic.player.domain;

/**
 * @Author: liguorui
 * @Date: 2021/1/18 下午11:04
 */
public enum OperationType {
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
}
