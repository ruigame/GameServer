package com.game.file.extractor;

/**
 * @Author: liguorui
 * @Date: 2020/11/26 下午11:15
 */
public class CompositeKeyFactory {

    public static LongIntCompositeKey create(long key1, int key2) {
        return new LongIntCompositeKey(key1, key2);
    }

    public static IntIntCompositeKey create(int key1, int key2) {
        return new IntIntCompositeKey(key1, key2);
    }

    public static ObjectsCompositeKey create(Object[] objects) {
        return new ObjectsCompositeKey(objects);
    }
}
