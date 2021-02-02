package com.game.file.prop;

import com.game.file.ConfigPath;
import com.game.file.FileLoader;
import com.game.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 属性文件仓库
 * @Author: liguorui
 * @Date: 2020/11/27 上午1:04
 */
public class PropConfigStore {

    private static final ConcurrentHashMap<String, PropConfig> configMap = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(PropConfigStore.class);

    public static final PropConfig COMMON_CONFIG = getPropConfig(ConfigPath.Common.COMMON_PROPERTIES);

    public static final PropConfig SERVER_CONFIG = getPropConfig(ConfigPath.Independent.SERVER_PROPERTIES);

    public static final PropConfig CROSS_CONFIG = getPropConfig(ConfigPath.Independent.CROSS_PROPERTIES);

    public static final PropConfig BACKEND_SUPPORT_CONFIG = getPropConfig(ConfigPath.Independent.BACKEND_PROPERTIES);

    public static PropConfig getPropConfig(String configPath) {
        PropConfig config = configMap.get(configPath);
        if (config == null) {
            synchronized (configMap) {
                config = configMap.get(configPath);
                if (config == null) {
                    try {
                        config = new PropConfig();
                        FileLoader.load(configPath, config);
                        configMap.put(configPath, config);
                    } catch (Exception e) {
                        ExceptionUtils.log(e);
                    }
                }
            }
        }
        return config;
    }

    /**
     * 预先加载
     */
    public static void inAdvanceLoad() {
        for (Field field : ConfigPath.class.getFields()) {
            try {
                String fileValue = field.get(null).toString();
                if (org.apache.commons.lang.StringUtils.endsWithIgnoreCase(fileValue, ".properties")) {
                    PropConfigStore.getPropConfig(fileValue);
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }
}
