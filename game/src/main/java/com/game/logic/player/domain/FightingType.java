package com.game.logic.player.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 战力类型，细分战力计算，为了战力计算更快
 * @Author: liguorui
 * @Date: 2021/1/19 下午9:46
 */
public enum  FightingType {

    /**
     * 神兵
     */
    ShenBing(1),
    /**
     * 羽翼
     */
    Wing(2),
    /**
     * 转职
     */
    zhuanzhi(3),
    ;

    private final int code;

    private static FightingType[] elements = values();

    static {
        String result = Arrays.stream(elements).map(FightingType::getCode)
                .collect(Collectors.groupingBy(r -> r, Collectors.counting()))
                .entrySet()
                .stream().filter(r -> r.getValue() > 1)
                .map(r -> "FightingType的ID[" + r.getKey() + " ]出现重复")
                .collect(Collectors.joining("\n"));
        if (StringUtils.isNotBlank(result)) {
            throw new RuntimeException(result);
        }
    }

    FightingType(int code) {
        this.code = code;
    }

    public static FightingType valueOf(int code) {
        for (FightingType fightingType : elements) {
            if (fightingType.getCode() == code) {
                return fightingType;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }
}
