package com.game.util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: liguorui
 * @Date: 2021/1/25 下午10:52
 */
public class RandomUtils {

    /**
     * 等概率随机一个
     * @param randList
     * @param <T>
     * @return
     */
    public static <T> T randEqualPro(List<T> randList) {
        if(randList.size() == 0) return null;
        int index = ThreadLocalRandom.current().nextInt(randList.size());
        return randList.get(index);
    }

    public static boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
