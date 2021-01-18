package com.game.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/12/8 下午9:46
 */
public abstract class TypeReference<T> {

    private final Type type;

    protected TypeReference() {
        Type superClass = getClass().getGenericSuperclass();
        type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
    }

    public TypeReference(Type type) {
        super();
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public final static TypeReference<List<Integer>> LIST_INTEGER = new TypeReference<List<Integer>>() {};

    public static final TypeReference<Map<Integer, Integer>> Integer2IntergerMap = new TypeReference<Map<Integer, Integer>>() {};
}
