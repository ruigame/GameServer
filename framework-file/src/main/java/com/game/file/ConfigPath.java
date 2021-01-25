package com.game.file;

/**
 * @Author: liguorui
 * @Date: 2020/11/27 上午1:10
 */
public class ConfigPath {

    public static final String BASE_PARH = "config";

    /**
     * 常用配置
     */
    public static final String COMMON_PROPERTIES = BASE_PARH + "/common/commons.properties";

    /**
     * java system属性
     */
    public static final String SYSTEM_PROPERTIES = BASE_PARH + "/common/system.properties";

    /**
     * DEBUG配置
     */
    public static final String DEBUG_PROPERTIES = BASE_PARH + "/independent/debug.properties";

    /**
     * GM配置
     */
    public static final String GM_PROPERTIES = BASE_PARH + "/independent/gm.properties";

    /**
     * 安全验证及加密
     */
    public static final String AUTH_PROPERTIES = BASE_PARH + "/independent/auth.properties";

    /**
     * 网络设置
     */
    public static final String NET_PROPERTIES = BASE_PARH + "/independent/net.properties";

    /**
     * 服务器配置
     */
    public static final String SERVER_PROPERTIES = BASE_PARH + "/independent/server.properties";

    /**
     * 白名单配置
     */
    public static final String WHITELIST_PROPERTIES = BASE_PARH + "/independent/whiteList.properties";

    /**
     * IP限制
     */
    public static final String IP_LIMIT = BASE_PARH + "/independent/whiteList.properties";

    /**
     * 跨服配置
     */
    public static final String CROSS_PROPERTIES = BASE_PARH + "/independent/cross.properties";
    /**
     * 后台配置
     */
    public static final String BACKEND_PROPERTIES = BASE_PARH + "/independent/backendSupport.properties";

    /**
     * 运维配置
     */
    public static final String YUN_WEI = BASE_PARH + "/independent/yunwei.properties";

    /**
     * 外挂检测配置
     */
    public static final String CHEAT = BASE_PARH + "/common/cheat.properties";

    /**
     * 场景数据配置
     */
    public static final String SCENE_PROPERTIES = BASE_PARH + "/common/scene.properties";

    /**
     * 聊天配置
     */
    public static final String CHAT_PROPERTIES = BASE_PARH + "/common/chat.properties";

    /**
     * 聊天检测
     */
    public static final String CHAT_CHECK = BASE_PARH + "/independent/chatCheck.properties";

    /**
     * 其他配置
     */
    public static final String MISC_PROPERTIES = BASE_PARH + "/common/misc.properties";

    /**
     * 外挂检测
     */
    public static final String PLUG_PROPERTIES = BASE_PARH + "/common/plug.properties";

    /**
     * 随机取名配置
     */
    public static final String RANDOM_NAME_PATH = BASE_PARH + "/common/ConfigRandomName.xml";
}
