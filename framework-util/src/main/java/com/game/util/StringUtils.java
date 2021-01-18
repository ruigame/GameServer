package com.game.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2021/1/14 下午9:49
 */
public class StringUtils {

    public static List<Integer> toIntegetListBySplit(String param, char split) {
        String[] strArrays = org.apache.commons.lang3.StringUtils.split(param, split);
        if (strArrays == null || strArrays.length == 0) {
            return Collections.emptyList();
        }
        List<Integer> integerList = new ArrayList<>();
        for (String str : strArrays) {
            integerList.add(Integer.parseInt(str));
        }
        return integerList;
    }
}
