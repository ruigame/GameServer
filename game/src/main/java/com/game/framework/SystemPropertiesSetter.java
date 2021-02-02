package com.game.framework;

import com.game.file.ConfigPath;
import com.game.file.prop.PropConfig;
import com.game.file.prop.PropConfigListener;
import com.game.file.prop.PropConfigStore;

import java.util.Map;

/**
 * 掉起加载所有的.properties文件
 * @Author: liguorui
 * @Date: 2020/12/14 下午8:32
 */
public class SystemPropertiesSetter {

    private static final PropConfig PROP_CONFIG = PropConfigStore.getPropConfig(ConfigPath.Common.SYSTEM_PROPERTIES);

    public static void setSystemProp() {
        setSystemProp0();
        PROP_CONFIG.addListener(new PropConfigListener() {
            @Override
            public void reload(PropConfig propConfig) {
                setSystemProp0();
            }
        });
    }

    private static void setSystemProp0() {
        for (Map.Entry<String, String> entry : PROP_CONFIG.toMap().entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }
}
