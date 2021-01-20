package com.game;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午1:21
 */
public class PacketId {

    public static final class Base {
        /**
         * 请求验证登陆
         */
        public static final short REQ_LOGIN_AUTH = 1;
        /**
         * 返回验证信息
         */
        public static final short RESP_LOGIN_AUTH = 2;
        /**
         * 请求随机名(分性别)
         */
        public static final short REQ_NAME_RANDOM = 3;
        /**
         * 返回随机名（分性别）
         */
        public static final short RESP_NAME_RANDOM = 4;
        /**
         * 请求创建角色
         */
        public static final short REQ_PLAYER_CREATE = 5;
        /**
         * 返回创建角色结果
         */
        public static final short RESP_CREATE_PLAYER = 6;
        /**
         * 请求登陆，发送消息
         */
        public static final short REQ_LOGIN_ASK = 7;
        /**
         * 返回服务器时间
         */
        public static final short RESP_SERVER_TIME = 8;
        /**
         * 请求断线重连
         */
        public static final short REQ_RECONNECT = 9;
        /**
         * 返回断线重连
         */
        public static final short RESP_RECONNECT = 10;
        /**
         * 请求系统心跳
         */
        public static final short REQ_SYSTEM_HEARTBEAT = 11;

        /**
         * 登陆重登陆
         */
        public static final short RESP_LOGIN_CONFLICT = 111;

        /**
         * 请求随机名(不分性别，仅供展示)
         */
        public static final short REQ_NAME_RANDOM_SHOW = 112;
        /**
         * 返回随机名（不分性别，仅供展示）
         */
        public static final short RESP_NAME_RANDOM_SHOW = 113;
    }

    /**
     * 玩家角色
     */
    public static final class Role {
        /**
         * 主角信息
         */
        public static final short RESP_MAIN_ROLE = 1001;
        /**
         * 开启新信息
         */
        public static final short REQ_ROLE_CREATE = 1002;
        /**
         * 返回开启新角色结果
         */
        public static final short RESP_ROLE_CREATE = 1003;
        /**
         * 返回玩家经济资源信息同步
         */
        public static final short RESP_RESOURCE_SYNC = 1004;
        /**
         * 返回玩家角色等级变化同步
         */
        public static final short RESP_LEVEL = 1005;
        /**
         * 返回玩家当前经验
         */
        public static final short RESP_EXP = 1006;
        /**
         * 玩家AVATAR外观形象更新
         */
        public static final short RESP_ROLE_AVATAR_CHANGE = 1007;
        /**
         * 请求显示角色
         */
        public static final short REQ_SHOW_ROLE = 1008;
        /**
         * 返回显示角色
         */
        public static final short RESP_SHOW_ROLE = 1009;
        /**
         * 请求查看其他玩家信息
         */
        public static final short REQ_SHOW_OTHER_PLAYER = 1010;
        /**
         * 返回查看其他玩家信息
         */
        public static final short RESP_SHOW_OTHER_PLAYER = 1011;
        /**
         * 请求使用改名卡改名
         */
        public static final short REQ_RENAME = 1012;
        /**
         * 返回使用改名卡改名
         */
        public static final short RESP_RENAME = 1013;
    }

    /***********【通用的】 **********/
    public static final class Utils {
        /**
         * 返回各种文字消息
         */
        public static final short RESP_MESSAGE = 9000;
        /**
         * 账号重登陆
         */
        public static final short RESP_LOGIN_CONFLICT = 9001;
    }


    /***************后台admin包**********************/
    public static final class Admin {
        /**
         * 请求后台测试
         */
        public static final short REQ_ADMIN_TEST = 1;
        /**
         * 返回结果
         */
        public static final short RESP_RESULT_PACKET = 2;
    }

    /***************跨服cross包**********************/
    public static final class Cross {
        /**
         * 跨服PING
         */
        public static final short CROSS_PING = 1;
    }
}
