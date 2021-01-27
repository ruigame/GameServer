package com.game.file;

/**
 * @Author: liguorui
 * @Date: 2020/11/27 上午1:10
 */
public interface ConfigPath {

    String BASE_PARH = "config";

    interface BadWord {
        /**
         * 聊天屏蔽字路径
         */
        String CHEAT_BADWORD_FILE = "constant/words.txt";
        /**
         * 聊天屏蔽字白名单
         */
        String CHEAT_WHITE_FILE = "constant/white_words.txt";
        /**
         * 名称屏蔽字路径
         */
        String NAME_BADWORD_FILE = "constant/name_words.txt";
        /**
         * 公会名称屏蔽字路径
         */
        String GUILD_BADWORD_FILE = "constant/guild_words.txt";
    }

    interface Common {
        /**
         * 常用配置
         */
        String COMMON_PROPERTIES = BASE_PARH + "/common/commons.properties";
        /**
         * java system属性
         */
        String SYSTEM_PROPERTIES = BASE_PARH + "/common/system.properties";
        /**
         * cron任务配置
         */
        String CRON_TASK_CONFIG_PROPERTIES = BASE_PARH + "/common/cronTaskConfig.properties";
        /**
         * 改名卡改名白名单
         */
        String ROLE_NAME_WHITE_LIST_PROPERTIES = BASE_PARH + "/common/roleNameWhiteList.properties";
        /**
         * 外挂检测配置
         */
        String CHEAT = BASE_PARH + "/common/cheat.properties";
        /**
         * 其他配置
         */
        String MISC_PROPERTIES = BASE_PARH + "/common/misc.properties";
        /**
         * 外挂检测
         */
        String PLUG_PROPERTIES = BASE_PARH + "/common/plug.properties";
        /**
         * 场景数据配置
         */
        String SCENE_PROPERTIES = BASE_PARH + "/common/scene.properties";
    }

    interface Independent {
        /**
         * DEBUG配置
         */
        String DEBUG_PROPERTIES = "independent/debug.properties";

        /**
         * GM配置
         */
        String GM_PROPERTIES = "independent/gm.properties";
        /**
         * 安全验证及加密
         */
        String AUTH_PROPERTIES = "independent/auth.properties";
        /**
         * 网络设置
         */
        String NET_PROPERTIES = "independent/net.properties";
        /**
         * 服务器配置
         */
        String SERVER_PROPERTIES = "independent/server.properties";

        /**
         * 白名单配置
         */
        String WHITELIST_PROPERTIES = "independent/whiteList.properties";
        /**
         * IP限制
         */
        String IP_LIMIT = "independent/ipLimit.properties";
        /**
         * 后台配置
         */
        String BACKEND_PROPERTIES = "independent/backendSupport.properties";
        /**
         * 跨服配置
         */
        String CROSS_PROPERTIES = "independent/cross.properties";
        /**
         * 聊天检测
         */
        String CHAT_CHECK = "independent/chatCheck.properties";
        /**
         * 运维配置
         */
        String YUN_WEI = "independent/yunwei.properties";
    }

    interface RandomName {
        /**
         * 随机取名配置
         */
        String RANDOM_NAME_PATH = BASE_PARH + "/common/ConfigRandomName.xml";
    }
}
