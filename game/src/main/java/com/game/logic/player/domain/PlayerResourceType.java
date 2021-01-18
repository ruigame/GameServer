package com.game.logic.player.domain;

import com.game.base.PlayerActor;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家资源
 * @Author: liguorui
 * @Date: 2021/1/18 下午10:57
 */
public enum PlayerResourceType {

    EXP(1, "经验") {
        @Override
        public boolean addValue(PlayerActor playerActor, long value, OperationType opType) {
            return super.addValue(playerActor, value, opType);
        }

        @Override
        public boolean deductValue(PlayerActor playerActor, long value, OperationType opType) {
            return super.deductValue(playerActor, value, opType);
        }
    },
    GOLD(2, "元宝"),
    COPPER(3, "铜钱"),
    INTED_GOLD(4, "代币元宝"),
    ;

    private final String desc;

    private final int code;

    @Override
    public String toString() {
        return name();
    }

    public long getValue(PlayerActor playerActor) {
        throw new UnsupportedOperationException(getDesc());
    }

    public boolean haveEnough(PlayerActor playerActor, long value) {
        return getValue(playerActor) >= value;
    }

    public boolean addValue(PlayerActor playerActor, long value, OperationType opType) {
        throw new UnsupportedOperationException(getDesc());
    }

    public boolean deductValue(PlayerActor playerActor, long value, OperationType opType) {
        throw new UnsupportedOperationException(getDesc());
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }

    private static Map<Integer, PlayerResourceType> idMap = new HashMap<>();
    private static Map<String, PlayerResourceType> nameMap = new HashMap<>();

    public static final PlayerResourceType[] elements = values();

    static {
        for (PlayerResourceType type : elements) {
            if (type.getCode() != 0) {
                if (idMap.containsKey(type.getCode())) {
                    throw new RuntimeException("PlayerResourceType error typeId: " + type.getCode() + ", name:"+ type.name() + ",desc:" + type.getDesc());
                }
                if (nameMap.containsKey(type.name())) {
                    throw new RuntimeException("PlayerResourceType error typeId: " + type.getCode() + ", name:"+ type.name() + ",desc:" + type.getDesc());
                }
                idMap.put(type.getCode(), type);
                nameMap.put(type.name(), type);
            }
        }
    }

    public static PlayerResourceType getById(int id) {
        PlayerResourceType res = idMap.get(id);
        return res;
    }

    public static PlayerResourceType getByName(String name) {
        PlayerResourceType res = nameMap.get(name);
        return res;
    }



    PlayerResourceType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
