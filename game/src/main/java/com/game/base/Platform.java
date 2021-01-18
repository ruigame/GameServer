package com.game.base;

/**
 * @Author: liguorui
 * @Date: 2021/1/13 下午10:41
 */
public enum  Platform {

    P_360(1, "360", "360平台"),
    ;

    private final int id;
    private final String code;
    private final String name;

    Platform(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public static Platform getByCode(String code) {
        for (Platform platform : Platform.values()) {
            if (platform.getCode() == code) {
                return platform;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
