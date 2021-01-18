package com.game.logic.player.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 战斗属性
 * @Author: liguorui
 * @Date: 2021/1/18 下午10:27
 */
public enum ResourceType {

    hp(1, "生命上限"),

    mp(2, "魔法上限"),

    att(3, "攻击力"),

    wuDef(4, "物理防御"),

    moDef(5, "魔法防御"),

    hpFac(6, "最大生命加成比例"),

    mpFac(7, "最大魔法加成比例"),

    attRecRa(8, "吸血几率"),

    attRecFac(9, "吸血比例"),

    recover(10, "生命回复"),

    rallyRa(11, "反伤几率"),

    rallyFac(12, "反伤比例"),

    dodgeRa(13, "闪避"),

    hitRa(14, "准确"),

    mabiRa(15, "麻痹几率"),

    mabiReRa(16, "麻痹抵抗"),

    redDefRa(17, "减防几率"),

    redDefFac(18, "减防比例"),

    redAttRa(19, "减攻几率"),

    redAttFac(20, "减攻比例"),

    critRa(21, "暴击几率"),

    critRedRa(22, "抗暴几率"),

    critFac(23, "暴击伤害加成"),

    critRedFac(24, "暴击抵抗"),

    damFac(25, "伤害加成"),

    damRed(26, "伤害减免"),
    ;

    private final String desc;

    private final int code;

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }

    private static Map<Integer, ResourceType> idMap = new HashMap<>();
    private static Map<String, ResourceType> nameMap = new HashMap<>();

    public static final ResourceType[] elements = values();

    static {
        for (ResourceType type : elements) {
            if (type.getCode() != 0) {
                if (idMap.containsKey(type.getCode())) {
                    throw new RuntimeException("ResourceType error typeId: " + type.getCode() + ", name:"+ type.name() + ",desc:" + type.getDesc());
                }
                if (nameMap.containsKey(type.name())) {
                    throw new RuntimeException("ResourceType error typeId: " + type.getCode() + ", name:"+ type.name() + ",desc:" + type.getDesc());
                }
                idMap.put(type.getCode(), type);
                nameMap.put(type.name(), type);
            }
        }
    }

    public static ResourceType getById(int id) {
        ResourceType res = idMap.get(id);
        return res;
    }

    public static ResourceType getByName(String name) {
        ResourceType res = nameMap.get(name);
        return res;
    }



    ResourceType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
