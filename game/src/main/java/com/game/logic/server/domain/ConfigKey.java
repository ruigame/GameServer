package com.game.logic.server.domain;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 下午10:34
 */
public enum  ConfigKey {

    /**
     * 开服时间
     */
    OPEN_SERVER_TIME {
        @Override
        public String toString() {
            return "openServerTime";
        }
    },

    /**
     * 开服时间设置标识
     */
    OPEN_SERVER_TIME_FLAG {
        @Override
        public String toString() {
            return "openServerTimeFlag";
        }
    },

    /**
     * 是否需要执行合服操作
     */
    NEED_COMBINE {
        @Override
        public String toString() {
            return "needCombine";
        }
    },

    /**
     * 合服时间
     */
    COMBINE_TIME {
        @Override
        public String toString() {
            return "combineTime";
        }
    },

    /**
     * 上次每日重置时间
     */
    LAST_DAILY_RESET_TIME {
        @Override
        public String toString() {
            return "last_daily_reset_time";
        }
        @Override
        public String getDefaultValue() {
            return "0";
        }
    },

    /**
     * 合服数
     */
    COMBINE_NUM {
        @Override
        public String toString() {
            return "combineNum";
        }
        @Override
        public String getDefaultValue() {
            return "0";
        }
    },

    /**
     * 保留的世界频道聊天
     */
    WORLD_PERSIST_CHAT {
        @Override
        public String toString() {
            return "worldPersistChat";
        }
        @Override
        public String getDefaultValue() {
            return "{}";
        }
    },

    /**
     * 跨服分组时间
     */
    CROSS_GROUP_TIME {
        @Override
        public String toString() {
            return "crossGroupTime";
        }
        @Override
        public String getDefaultValue() {
            return "0";
        }
    },

    /**
     * 暂停跨服
     */
    PAUSE_CROSS {
        @Override
        public String toString() {
            return "pauseCross";
        }
        @Override
        public String getDefaultValue() {
            return "false";
        }
    },

    /**
     * 跨服时间
     */
    CROSS_TIME {
        @Override
        public String toString() {
            return "crossTime";
        }
        @Override
        public String getDefaultValue() {
            return "0";
        }
    },

    /**
     * 保留的跨服频道聊天
     */
    CROSS_PERSIST_CHAT {
        @Override
        public String toString() {
            return "crossPersistChat";
        }
        @Override
        public String getDefaultValue() {
            return "{}";
        }
    },

    /**
     * 测试数据
     */
    CESHI_DATA {
        @Override
        public String toString() {
            return "ceshi";
        }
        @Override
        public String getDefaultValue() {
            return "{}";
        }
    },
    ;

    /**
     * 获取插入到数据库的默认值
     * @return
     */
    public String getDefaultValue() {
        return "";
    }

    public static ConfigKey getConfigKey(String str) {
        for (ConfigKey configKey : values()) {
            if (configKey.toString().equals(str)) {
                return configKey;
            }
        }
        return null;
    }
}
