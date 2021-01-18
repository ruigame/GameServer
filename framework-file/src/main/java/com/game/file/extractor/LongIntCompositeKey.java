package com.game.file.extractor;

/**
 * @Author: liguorui
 * @Date: 2020/11/26 下午11:15
 */
public class LongIntCompositeKey implements CompositeKey{

    private final long key1;
    private final int key2;

    public LongIntCompositeKey(long key1, int key2) {
        super();
        this.key1 = key1;
        this.key2 = key2;
    }

    public long getKey1() {
        return key1;
    }

    public int getKey2() {
        return key2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int)(key1 ^ (key1 >>> 32));
        result = prime * result + key2;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LongIntCompositeKey other = (LongIntCompositeKey)obj;
        if (key1 != other.key1)
            return false;
        if (key2 != other.key2)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "LongIntCompositeKey{" +
                "key1=" + key1 +
                ", key2=" + key2 +
                '}';
    }
}
