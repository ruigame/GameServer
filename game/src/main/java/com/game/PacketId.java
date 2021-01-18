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
         * 请求随机名
         */
        public static final short REQ_NAME_RANDOM = 3;
        /**
         * 返回随机名
         */
        public static final short RESP_NAME_RANDOM = 4;
        /**
         * 请求创建角色
         */
        public static final short REQ_CREATE_PLAYER = 5;
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
    }

    /***********【通用的】 **********/
    public static final class Utils {
        /**
         * 返回各种文字消息
         */
        public static final short RESP_MESSAGE = 2;
        /**
         * 账号重登陆
         */
        public static final short RESP_LOGIN_CONFLICT = 2;
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
