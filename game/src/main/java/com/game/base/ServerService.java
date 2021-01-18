package com.game.base;

import com.game.file.ConfigPath;
import com.game.file.prop.PropConfig;
import com.game.file.prop.PropConfigStore;
import com.game.util.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: liguorui
 * @Date: 2021/1/14 下午9:42
 */
@Component
public class ServerService implements InitializingBean {

    public static final long PLATFORM_OFFSET = 1000000000000L;
    public static final long SERVER_OFFSET = 10000000L;

    /**
     * 初始的服务器标示
     */
    private String oriServerFlag;

    /**
     * 初始的服务器id
     */
    private int oriServerId;

    /**
     * 该服务器运行了哪些合服
     */
    private Set<Integer> holdingServerSet;

    /**
     * 平台Id
     */
    private int platformId;

    @Override
    public void afterPropertiesSet() throws Exception {
        PropConfig propConfig = PropConfigStore.getPropConfig(ConfigPath.SERVER_PROPERTIES);
        oriServerFlag = propConfig.getStr("ORI_SERVER_FLAG");
        oriServerId = propConfig.getInt("ORI_SERVER_ID");
        platformId = propConfig.getInt("PLATFORM_ID");
        this.holdingServerSet = new HashSet<>(StringUtils.toIntegetListBySplit(propConfig.getStr("HOLDING_SERVERS"), ','));
    }

    public String getOriServerFlag() {
        return oriServerFlag;
    }

    public int getOriServerId() {
        return oriServerId;
    }

    public Set<Integer> getHoldingServerSet() {
        return holdingServerSet;
    }

    public int getPlatformId() {
        return platformId;
    }
}

