package com.game.logic.desc.domain;

import com.game.util.Unique;

/**
 * @Author: liguorui
 * @Date: 2021/1/20 下午9:19
 */
public enum MsgId implements Unique {

    /**
     * 文字提示
     */
    WORD_MSG(100),
    /**
     * 等级不足
     */
    PLAYRER_LEVEL_NOT(101),
    /**
     * {0}的装备升到{1}级了
     */
    EQUIP_LEVEL(102),
     ;

    static {
        for (MsgId msgId : values()) {
            if (msgId.getId() >Short.MAX_VALUE) {
                throw new IllegalArgumentException(msgId.getId() + " too max");
            }
        }
    }

    private int id;

    MsgId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static MsgId getById(int id) {
        for (MsgId msgId : values()) {
            if (msgId.getId() == id) {
                return msgId;
            }
        }
        return null;
    }

    @Override
    public Object uniqueID() {
        return id;
    }
}
