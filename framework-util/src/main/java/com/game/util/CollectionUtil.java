package com.game.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/11/24 下午10:07
 */
public class CollectionUtil {

    public static boolean isNotEmpty(Map<?, ?> map) {
        return MapUtils.isNotEmpty(map);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return MapUtils.isEmpty(map);
    }

    public static boolean isEmpty(Collection<? extends Object> c) {
        return CollectionUtils.isEmpty(c);
    }

    public static void addIntegerMap(Map<Integer, Integer> totalMap, int key, int value) {
        Integer oldValue = totalMap.get(key);
        totalMap.put(key, oldValue == null ? value : oldValue + value);
    }

    public static <K> void addIntegerMap(Map<K, Integer> totalMap, Map<K, Integer> addMap) {
        if (isEmpty(addMap)) return;
        for (Map.Entry<K, Integer> entry : addMap.entrySet()) {
            Integer newSum = entry.getValue();
            Integer oldSum = totalMap.get(entry.getKey());
            if (oldSum != null) {
                newSum += oldSum.intValue();
            }
            totalMap.put(entry.getKey(), newSum);
        }
    }

    public static <K> Map<K, Integer> deductIntegerMap(Map<K, Integer> totalMap, Map<K, Integer> deductMap) {
        Map<K, Integer> newMap = new HashMap<>(totalMap);
        for (Map.Entry<K, Integer> entry : deductMap.entrySet()) {
            Integer newSUm = entry.getValue();
            Integer oldSum = newMap.get(entry.getKey());
            if (oldSum != null) {
                newSUm = oldSum.intValue() - newSUm.intValue();
                newMap.put(entry.getKey(), newSUm);
            }
        }
        return newMap;
    }

    public static char[] toLowerCase(char[]source) {
        char[] lowerChars = new char[source.length];
        for (int i = 0, len = source.length; i < len; i++) {
            lowerChars[i] = Character.toLowerCase(source[i]);
        }
        return lowerChars;
    }
}
