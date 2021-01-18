package com.game.logic.commdata;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/12/8 下午11:04
 */
public enum NewCommDataType {

    INSTEAD_TOTAL(1, "0", "代币充值累计值"),
    SHOP_DAILY_BUY(2, "{}", true, "商城每日购买记录"),
    OPEN_STATUS(3, "[]", "开启状态"),


    ;
    private final int type;
    private final String initValue;
    private final NewCommDataMonitor monitor;
    private final String desc;

    NewCommDataType(int type, String initValue, String desc) {
        this(type, initValue, null, desc);
    }

    NewCommDataType(int type, String initValue, boolean daily, String desc) {
        this(type, initValue, daily ? NewCommDataMonitor.DAILY : null, desc);
    }

    NewCommDataType(int type, String initValue, NewCommDataMonitor monitor, String desc) {
        this.type = type;
        this.initValue = initValue;
        this.monitor = monitor;
        this.desc = desc;
    }

    private static final Map<Integer, NewCommDataType> typeCatalog = Maps.newHashMap();

    static {
        for (NewCommDataType dataType : values()) {
            NewCommDataType oldType = typeCatalog.put(dataType.type, dataType);
            if (oldType != null) {
                throw new IllegalStateException(String.format("%s and %s have same type.", oldType, dataType));
            }
        }
    }

    public static NewCommDataType wrap(int type) {
        return typeCatalog.get(type);
    }

    public int getType() {
        return type;
    }

    public String getInitValue() {
        return initValue;
    }

    public NewCommDataMonitor getMonitor() {
        return monitor;
    }

    public String getDesc() {
        return desc;
    }
}
