package com.game.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 下午10:49
 */
public class JSONUtils {

    private final static String NULL_JSON_OBJECT_STR = "{}";

    private static final String PRO_SPLIT = ",";

    private static final char KV_SPLIT = ':';
    private static final char OBJ_START = '{';
    private static final char OBJ_END = '}';

    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    /**
     * 把一个map转成JSON字符串daoshih
     * @param map
     * @return
     */
    public static String toJsonString(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return NULL_JSON_OBJECT_STR;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(OBJ_START);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append('"');
            sb.append(entry.getKey().toString());
            sb.append('"');
            sb.append(KV_SPLIT);
            sb.append('"');
            sb.append(entry.getValue().toString());
            sb.append('"');
            sb.append(PRO_SPLIT);
        }
        return sb.substring(0, sb.lastIndexOf(PRO_SPLIT)) + OBJ_END;
    }

    public static List<Integer> jsonToIntegerList(String json) {
        return toObject(json, TypeReference.LIST_INTEGER);
    }

    public static List<Integer> jsonToIntegerList(JSONArray jsonArray) {
        return Arrays.asList(jsonArray.toArray(new Integer[0]));
    }

    private static String replace(String json) {
        json = StringUtils.replace(json, "，", ",");
        json = StringUtils.replace(json, "：", ":");
        json = StringUtils.replace(json, "；", ";");
        return json;
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        return JSON.parseObject(json, typeReference.getType());
    }

    public static String toJsonStr(Object obj) {
        if (obj instanceof Map) {
            if (obj == null || ((Map) obj).isEmpty()) {
                return NULL_JSON_OBJECT_STR;
            }
        }
        return JSON.toJSONString(obj);
    }

    public static String toJsonStr(Object obj, SerializerFeature... features) {
        return JSON.toJSONString(obj, features);
    }

    public static String toJsonStr(Object obj, SerializeFilter filter) {
        return JSON.toJSONString(obj, filter, new SerializerFeature[0]);
    }

    public static Map<Integer, Integer> mapFromJsonInteger(String json) {
        json = StringUtils.trimToNull(json);
        if (StringUtils.isBlank(json)) {
            return Collections.emptyMap();
        }
        json = replace(json);
        return toObject(json, TypeReference.Integer2IntergerMap);
    }
}
