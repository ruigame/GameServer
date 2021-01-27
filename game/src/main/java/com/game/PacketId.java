package com.game;

/**
 * 包协议号 = 模块ID * 100 + 自增
 * @Author: liguorui
 * @Date: 2020/12/4 下午1:21
 */
public interface PacketId {

    interface Base {
        /**
         * 请求验证登陆
         */
        short REQ_LOGIN_AUTH = 10101;
        /**
         * 返回登陆验证信息
         */
        short RESP_LOGIN_AUTH = 10102;
        /**
         * 请求随机名(分性别)
         */
        short REQ_NAME_RANDOM = 10103;
        /**
         * 返回随机名（分性别）
         */
        short RESP_NAME_RANDOM = 10104;
        /**
         * 请求创建角色
         */
        short REQ_PLAYER_CREATE = 10105;
        /**
         * 返回创建角色结果
         */
        short RESP_CREATE_PLAYER = 10106;
        /**
         * 请求登陆，发送消息
         */
        short REQ_LOGIN_ASK = 10107;
        /**
         * 返回服务器时间
         */
        short RESP_SERVER_TIME = 10108;
        /**
         * 请求断线重连
         */
        short REQ_RECONNECT = 10109;
        /**
         * 返回断线重连
         */
        short RESP_RECONNECT = 10110;
        /**
         * 请求系统心跳
         */
        short REQ_SYSTEM_HEARTBEAT = 10111;

        /**
         * 账号重登陆
         */
        short RESP_LOGIN_CONFLICT = 10112;

        /**
         * 请求随机名(不分性别，仅供展示)
         */
        short REQ_NAME_RANDOM_SHOW = 10113;
        /**
         * 返回随机名（不分性别，仅供展示）
         */
        short RESP_NAME_RANDOM_SHOW = 10114;
        /**
         * 返回封号倒计时
         */
        short RESP_FORBIDED_TIME_LESS = 10115;
        /**
         * 返回通知前端初始化的模块基础数据已经加载完毕，方便客户端确认模块开启
         */
        short RESP_MODULE_INIT_END = 10116;
        /**
         * 请求查看当前服务器版本号
         */
        short REQ_VERSION = 10117;
        /**
         * 返回查看当前服务器版本号
         */
        short RESP_VERSION = 10118;
    }

    /**
     * 玩家角色
     */
    interface Role {
        /**
         * 主角信息
         */
        short RESP_MAIN_ROLE = 1001;
        /**
         * 开启新信息
         */
        short REQ_ROLE_CREATE = 1002;
        /**
         * 返回开启新角色结果
         */
        short RESP_ROLE_CREATE = 1003;
        /**
         * 返回玩家经济资源信息同步
         */
        short RESP_RESOURCE_SYNC = 1004;
        /**
         * 返回玩家角色等级变化同步
         */
        short RESP_LEVEL = 1005;
        /**
         * 返回玩家当前经验
         */
        short RESP_EXP = 1006;
        /**
         * 玩家AVATAR外观形象更新
         */
        short RESP_ROLE_AVATAR_CHANGE = 1007;
        /**
         * 请求显示角色
         */
        short REQ_SHOW_ROLE = 1008;
        /**
         * 返回显示角色
         */
        short RESP_SHOW_ROLE = 1009;
        /**
         * 请求查看其他玩家信息
         */
        short REQ_SHOW_OTHER_PLAYER = 1010;
        /**
         * 返回查看其他玩家信息
         */
        short RESP_SHOW_OTHER_PLAYER = 1011;
        /**
         * 请求使用改名卡改名
         */
        short REQ_RENAME = 1012;
        /**
         * 返回使用改名卡改名
         */
        short RESP_RENAME = 1013;
    }

    /***********【通用的】 **********/
    interface Utils {
        /**
         * 返回各种文字消息
         */
        short RESP_MESSAGE = 9000;
        /**
         * 账号重登陆
         */
        short RESP_LOGIN_CONFLICT = 9001;
    }


    /***************后台admin包**********************/
    interface Admin {
        /**
         * 请求后台测试
         */
        short REQ_ADMIN_TEST = 1;
        /**
         * 返回结果
         */
        short RESP_RESULT_PACKET = 2;
        /**
         * 根据角色账号踢下线
         */
        short REQ_ADMIN_KICK_BY_ACCOUNT = 3;
        /**
         * 踢全服玩家下线
         */
        short REQ_ADMIN_KICKALL = 4;
        /**
         * 后台发放元宝
         */
        short REQ_ADMIN_SEND_GOLD = 5;
        /**
         * 服务器开启
         */
        short REQ_ADMIN_OPEN_SERVER = 6;
    }

    /***************跨服cross包**********************/
    interface Cross {
        /**
         * 跨服PING
         */
        short CROSS_PING = 1;
    }
}
