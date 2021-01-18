package com.game.file.prop;

import com.game.file.MergerReloadListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 封装.properties类型文件的熟悉，通过PropConfigStore
 * @Author: liguorui
 * @Date: 2020/11/27 上午12:44
 */
public class PropConfig extends MergerReloadListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropConfig.class);

    private volatile Map<String, String> map = new HashMap<>();

    private Set<PropConfigListener> listenerSet = new CopyOnWriteArraySet<>();

    public void init(InputStream inputStream) {
        try {
            Map<String, String> tempMap = new HashMap<>();
            Properties properties = new Properties();
            Reader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            properties.load(reader);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = StringUtils.trimToNull(entry.getKey().toString());
                if (key == null) {
                    continue;
                }
                tempMap.put(key, StringUtils.trimToNull(entry.getValue().toString()));
            }
            this.map = tempMap;
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public int getInt(String key) {
        return Integer.parseInt(getStr(key));
    }

    public int getInt(String key, int defaultValue) {
        String value = getStr(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public long getLong(String key) {
        return Long.parseLong(getStr(key));
    }

    public long getLong(String key, long defaultValue) {
        String value = getStr(key);
        return value != null ? Long.parseLong(value) : defaultValue;
    }

    public String getStr(String key) {
        return map.get(key);
    }

    public String getStr(String key, String defaultValue) {
        String value = map.get(key);
        return value == null ? defaultValue : value;
    }

    public boolean getBoolean(String key) {
        String value = getStr(key);
        return value != null ? Boolean.parseBoolean(key) : false;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getStr(key);
        return value != null ? Boolean.parseBoolean(key) : defaultValue;
    }

    public void addListener(PropConfigListener propConfigListener) {
        listenerSet.add(propConfigListener);
    }

    public Map<String, String> toMap() {
        return Collections.unmodifiableMap(map);
    }

    @Override
    public void load(InputStream inputStream) {
        init(inputStream);
        for (PropConfigListener listener : listenerSet) {
            listener.reload(this);
        }
    }
}
