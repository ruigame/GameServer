package com.game.base;

import com.game.logic.player.domain.PlayerResourceType;
import com.game.logic.player.domain.ResourceType;
import com.game.logic.player.domain.Role;
import com.game.logic.player.domain.Skill;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

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

    public final static TypeReference<List<Role>> LIST_ROLE = new TypeReference<List<Role>>() {};

    public static final TypeReference<Map<Integer, Integer>> Integer2IntergerMap = new TypeReference<Map<Integer, Integer>>() {};

    public final static TypeReference<Map<Integer, Map<Integer, Skill>>> MAP_SKILL = new TypeReference<Map<Integer, Map<Integer, Skill>>>(){};

    public static final TypeReference<EnumMap<PlayerResourceType, Long>> PLAYER_RESOURCETYPE_LONG = new TypeReference<EnumMap<PlayerResourceType, Long>>(){};

    public static final TypeReference<HashMap<ResourceType, Long>> MAP_RESOURCETYPE_LONG = new TypeReference<HashMap<ResourceType, Long>>(){};

    public static final TypeReference<Map<Integer, Integer>> integer2IntergerMap = new TypeReference<Map<Integer, Integer>>(){};

    public static final TypeReference<Map<Integer, Long>> integer2LongMap = new TypeReference<Map<Integer, Long>>(){};

    public static final TypeReference<Set<Integer>> integerSet = new TypeReference<Set<Integer>>(){};

    public static final TypeReference<Set<Long>> SET_LONG = new TypeReference<Set<Long>>(){};

    public static final TypeReference<Map<Integer, Map<ResourceType, Long>>> integer2attr = new TypeReference<Map<Integer, Map<ResourceType, Long>>>(){};

}
