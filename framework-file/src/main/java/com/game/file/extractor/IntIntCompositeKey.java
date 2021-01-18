package com.game.file.extractor;

/**
 * @Author: liguorui
 * @Date: 2020/11/26 下午11:15
 */
public class IntIntCompositeKey implements CompositeKey{

    private final int key1;
    private final int key2;

    public IntIntCompositeKey(int key1, int key2) {
        super();
        this.key1 = key1;
        this.key2 = key2;
    }

    public int getKey1() {
        return key1;
    }

    public int getKey2() {
        return key2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key1;
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
        IntIntCompositeKey other = (IntIntCompositeKey)obj;
        if (key1 != other.key1)
            return false;
        if (key2 != other.key2)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IntIntCompositeKey{" +
                "key1=" + key1 +
                ", key2=" + key2 +
                '}';
    }
}
