package com.game.file.extractor;

import java.util.Arrays;

/**
 * @Author: liguorui
 * @Date: 2020/11/26 下午11:15
 */
public class ObjectsCompositeKey implements CompositeKey{

    private Object[] objects;

    public ObjectsCompositeKey(Object[] objects) {
        super();
        this.objects = objects;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(objects);
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
        ObjectsCompositeKey other = (ObjectsCompositeKey)obj;
        if (!Arrays.equals(objects, other.objects))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ObjectsCompositeKey{" +
                "objects=" + Arrays.toString(objects) +
                '}';
    }
}
