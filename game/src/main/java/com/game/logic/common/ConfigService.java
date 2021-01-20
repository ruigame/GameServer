package com.game.logic.common;

import com.game.base.Platform;
import com.game.file.ConfigPath;
import com.game.file.prop.PropConfig;
import com.game.file.prop.PropConfigListener;
import com.game.file.prop.PropConfigStore;
import com.game.logic.player.PlayerService;
import com.game.logic.server.domain.ConfigKey;
import com.game.logic.server.entity.ServerInfo;
import com.game.logic.server.manager.ServerInfoManager;
import com.game.logic.sys.PlayerCreateListener;
import com.game.util.Context;
import com.game.util.GameSession;
import com.game.util.TimeUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 下午10:17
 */
@Component
public class ConfigService implements PlayerCreateListener {

    /**
     * 平台ID   区服ID   自增ID
     * 00033  0099001  0000001
     */
    public static final long PLATFORM_OFFSET = 10000000L;

    public static final long SERVER_OFFSET = 10000000L;

    @Autowired
    private ServerInfoManager configManager;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UUIDService uuidService;

    /**
     * 初始的服务器ID
     */
    private int oriServerId;

    /**
     * 该服务器运行了哪些合服
     */
    private Set<Integer> holdingServerSet;

    /**
     * 平台ID
     */
    private int platformId;

    /**
     * 平台
     */
    private String platform;

    /**
     * 开服时间
     */
    private int openTime;

    /**
     * 开服当天零点（为了计算开服天数）
     */
    private long openServerZeroTime;

    /**
     * 合服时间
     */
    private int combineTime;

    /**
     * 合服当天零点（为了计算合服天数）
     */
    private long combineTimeZeroTime;

    /**
     * 是否重新查询最大玩家ID
     */
    private boolean reinit = false;

    /**
     * 是否开启GM
     */
    private boolean GMEnabled = false;

    /**
     * 是否开启验证
     */
    private boolean authEnabled = false;

    /**
     * 是否开启加密
     */
    private boolean encryptEnabled = false;

    /**
     * 端口
     */
    private int port = 4010;

    /**
     * 场景数据路径
     */
    private String sceneDataPath = "";

    /**
     * 验证key
     */
    private String authKey;

    /**
     * 时间戳自增器
     */
    private AtomicLong idInc;

    /**
     * 白名单
     */
    private String[] patterns;

    /**
     * 服务器是否对外开放
     */
    private volatile boolean serverOpen = true;

    private boolean serverOpenTimeFlag = false;

    private ReentrantLock serverOpenTimeLock = new ReentrantLock();

    private int hookCheckThreshold = 1000;

    private int playerCountThreadshold = 30;

    private boolean flushStatus = true;

    private int plugThreashold = 3;

    private int plugInterval = 200;

    private Map<Short, Boolean> exludeId = Maps.newHashMap();

    private boolean accumDisOn = true;

    private int cheatAccumDur = 1000;

    private int maxAccumDis = 120;

    private int cheatCheckDur = 1000;

    private int maxCheatCount = 3;

    private int cheatHeartbeatDiff = 500;

    private int heartbeatInterval = 14000;

    private int maxCheatHearBeatCount = 2;

    private int heartbeatCheatDur = 30000;

    private int cheatHoldTime = 30000;

    private int kickOffCheatTime = 5;

    private int kickOffCheatTimeDur = 300000;

    private int singleCheatHeartbeatDiff = 4000;

    private int prisonOutTime;

    /**
     * 开启检查系统时间变化，刷新任务调度（测试时候才开启）
     */
    private boolean checkTimeChange;

    private String adminKey;

    private int ipLimitCount;
    private String[] ipLimitWhiteList;

    /**
     * 是否初始化
     */
    private boolean init;

    private void initDebug(PropConfig propConfig) {
        reinit = propConfig.getBoolean("REINIT");
        checkTimeChange = propConfig.getBoolean("CHECK_TIME_CHANGE");
    }

    private void initGM(PropConfig propConfig) {
        GMEnabled = propConfig.getBoolean("GM_ENABLED");
    }

    private void initAuth(PropConfig propConfig) {
        authEnabled = propConfig.getBoolean("AUTH_ENABLED");
        encryptEnabled = propConfig.getBoolean("ENCRYPT_ENABLED");
    }

    private void initNet(PropConfig propConfig) {
        port = propConfig.getInt("PORT");
    }

    private void initMISC(PropConfig propConfig) {
        authKey = propConfig.getStr("AUTH_KEY");
        hookCheckThreshold = propConfig.getInt("HOOK_CHECK_THRESHOLD");
        playerCountThreadshold = propConfig.getInt("PLAYER_COUNT_THRESHOLD");
        flushStatus = propConfig.getBoolean("FLUSH_STATUS");
    }

    private void initPlus(PropConfig propConfig) {
        plugThreashold = propConfig.getInt("PLUG_THRESHOLD");
        plugInterval = propConfig.getInt("PLUG_INTERVAL");
        String exludeIds = propConfig.getStr("PLUG_EXCLUDE");
        String[] ids = exludeIds.split(",");
        Map<Short, Boolean> exludeId = Maps.newHashMap();
        for (String id : ids) {
            exludeId.put(Short.parseShort(id), Boolean.TRUE);
        }
        this.exludeId = exludeId;
    }

    private void initServer(PropConfig propConfig) {
        oriServerId = propConfig.getInt("ORI_SERVER_ID");
        platformId = propConfig.getInt("PLATFORM_ID");
        this.platform = propConfig.getStr("PLATFORM");
        this.holdingServerSet = new HashSet<>();
        String holdServer = propConfig.getStr("HOLDING_SERVERS");
        String []serverArray = StringUtils.split(holdServer, ",");
        for (String serverId : serverArray) {
            holdingServerSet.add(Integer.parseInt(serverId));
        }
    }

    private void initScene(PropConfig propConfig) {
        sceneDataPath = propConfig.getStr("SCENE_DATA_PATH");
    }

    private void initWhiteList(PropConfig propConfig) {
        String wl = propConfig.getStr("WHITE_LIST");
        String []patterns = wl.split(",");
        this.patterns = patterns;
        this.adminKey = propConfig.getStr("ADMIN_KEY");
    }

    private void initIpLimit(PropConfig propConfig) {
        this.ipLimitCount = propConfig.getInt("IP_LIMIT_COUNT", Integer.MAX_VALUE);
        String wl = propConfig.getStr("IP_LIMIT_WHITE_LIST");
        String[] patterns = wl.split(",");
        this.ipLimitWhiteList = patterns;
    }

    private void initCheat(PropConfig propConfig) {
        this.accumDisOn = propConfig.getBoolean("ACCUM_DIS_ON");
        this.cheatAccumDur = propConfig.getInt("ACCUM_DUR");
        this.maxAccumDis = propConfig.getInt("MAX_ACCUM_DIS");
        this.cheatCheckDur = propConfig.getInt("CHEAT_CHECK_DUR");
        this.maxCheatCount = propConfig.getInt("MAX_CHEAT_COUNT");
        this.cheatHeartbeatDiff = propConfig.getInt("CHEAT_HEARTBEAT_DIFF");
        this.heartbeatInterval= propConfig.getInt("HEARTBEAT_INTERVAL");
        this.maxCheatHearBeatCount = propConfig.getInt("MAX_CHEAT_HEARTBEAT_COUNT");
        this.heartbeatCheatDur = propConfig.getInt("HEARTBEAT_CHEAT_DUR");
        this.cheatHoldTime = propConfig.getInt("CHEAT_HOLD_TIME");
        this.kickOffCheatTime = propConfig.getInt("KICK_OFF_CHEAT_TIME");
        this.kickOffCheatTimeDur = propConfig.getInt("KICK_OFF_CHEAT_TIME_DUR");
        this.singleCheatHeartbeatDiff = propConfig.getInt("SINGLE_CHEAT_HEARTBEAT_DIFF");
        this.prisonOutTime = propConfig.getInt("PRISON_OUT_TIME");
    }

    private void initProps() {
        PropConfig debug = PropConfigStore.getPropConfig(ConfigPath.DEBUG_PROPERTIES);
        initDebug(debug);
        debug.addListener(new PropConfigListener() {
            @Override
            public void reload(PropConfig propConfig) {
                initDebug(propConfig);
            }
        });

        PropConfig gm = PropConfigStore.getPropConfig(ConfigPath.GM_PROPERTIES);
        initGM(gm);
        gm.addListener(new PropConfigListener() {
            @Override
            public void reload(PropConfig propConfig) {
                initGM(propConfig);
            }
        });

        PropConfig auth = PropConfigStore.getPropConfig(ConfigPath.AUTH_PROPERTIES);
        initAuth(auth);
        auth.addListener(new PropConfigListener() {
            @Override
            public void reload(PropConfig propConfig) {
                initAuth(propConfig);
            }
        });

        PropConfig net = PropConfigStore.getPropConfig(ConfigPath.NET_PROPERTIES);
        initNet(net);
        net.addListener(new PropConfigListener() {
            @Override
            public void reload(PropConfig propConfig) {
                initNet(propConfig);
            }
        });

        PropConfig server = PropConfigStore.getPropConfig(ConfigPath.SERVER_PROPERTIES);
        initServer(server);
        server.addListener(new PropConfigListener() {
            @Override
            public void reload(PropConfig propConfig) {
                initServer(propConfig);
            }
        });

        PropConfig scene = PropConfigStore.getPropConfig(ConfigPath.SCENE_PROPERTIES);
        initScene(scene);
        scene.addListener(new PropConfigListener() {
            @Override
            public void reload(PropConfig propConfig) {
                initScene(propConfig);
            }
        });

        PropConfig misc = PropConfigStore.getPropConfig(ConfigPath.MISC_PROPERTIES);
        initMISC(misc);
        misc.addListener(new PropConfigListener() {
            @Override
            public void reload(PropConfig propConfig) {
                initMISC(propConfig);
            }
        });

        PropConfig wl = PropConfigStore.getPropConfig(ConfigPath.WHITELIST_PROPERTIES);
        initWhiteList(wl);
        wl.addListener(new PropConfigListener() {
            @Override
            public void reload(PropConfig propConfig) {
                initWhiteList(propConfig);
            }
        });

        PropConfig plug = PropConfigStore.getPropConfig(ConfigPath.PLUG_PROPERTIES);
        initPlus(plug);
        plug.addListener(new PropConfigListener() {
            @Override
            public void reload(PropConfig propConfig) {
                initPlus(propConfig);
            }
        });

        PropConfig cheat = PropConfigStore.getPropConfig(ConfigPath.CHEAT);
        initCheat(cheat);
        cheat.addListener(new PropConfigListener() {
            @Override
            public void reload(PropConfig propConfig) {
                initCheat(propConfig);
            }
        });

        PropConfig ipLimit = PropConfigStore.getPropConfig(ConfigPath.IP_LIMIT);
        initIpLimit(ipLimit);
        ipLimit.addListener(new PropConfigListener() {
            @Override
            public void reload(PropConfig propConfig) {
                initIpLimit(propConfig);
            }
        });
    }

    @PostConstruct
    public void initConfig() {
        configManager.init();
    }

    public void initAll() {
        initOpenTime();
        this.initServeropenTImeFlag();
        this.initProps();
        this.initUUID();
        this.initCombineTime();

        uuidService.initAll();
        this.init = true;
    }

    private void initOpenTime() {
        ServerInfo serverInfo = configManager.getConfig(ConfigKey.OPEN_SERVER_TIME);
        if (serverInfo.getIntValue() <= 0) {
            serverInfo.setCvalue(String.valueOf(TimeUtils.timestamp()));
            configManager.update(serverInfo);
        }
        this.openTime = serverInfo.getIntValue();
        this.openServerZeroTime = OnlineUtils.getDayStartMillis(openTime * 1000L);
    }

    private void initCombineTime() {
        ServerInfo serverInfo = configManager.getConfig(ConfigKey.COMBINE_TIME);
        this.combineTime = serverInfo.getIntValue();
        this.combineTimeZeroTime = combineTime == 0 ? 0 : OnlineUtils.getDayStartMillis(combineTime * 1000L);
    }

    public void updateCombineTime(int timestamp) {
        ServerInfo serverInfo = configManager.getConfig(ConfigKey.COMBINE_TIME);
        serverInfo.setCvalue(String.valueOf(timestamp));
        configManager.update(serverInfo);
        this.combineTime = timestamp;
        this.combineTimeZeroTime = OnlineUtils.getDayStartMillis(combineTime * 1000L);
    }

    private void initUUID() {
        String t = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        long id = Long.parseLong(t);
        idInc = new AtomicLong(id);
    }

    /**
     * 获取开服时间戳（秒）
     * @return
     */
    public int getOpenTimestamp() {
        Preconditions.checkArgument(init, "未初始化");
        return this.openTime;
    }

    /**
     * 修改开服时间
     * @param timeStamp
     */
    public void updateOpenTimestamp(long timeStamp) {
        ServerInfo config = configManager.getConfig(ConfigKey.OPEN_SERVER_TIME);
        config.setCvalue(String.valueOf(timeStamp));
        configManager.update(config);
        openTime = config.getIntValue();
        this.openServerZeroTime = OnlineUtils.getDayStartMillis(openTime * 1000L);
    }

    /**
     * 是否已经合服
     * @return
     */
    public boolean isCombined() {
        return combineTime >0;
    }

    private void initServeropenTImeFlag() {
        ServerInfo serverOpenTimeFlagInfo = configManager.getConfig(ConfigKey.OPEN_SERVER_TIME_FLAG);
        /**
         * 当已经存在的角色数大雨阀值代表已经设置了正式的开服时间
         */
        if (this.playerService.getExistPlayerCount() > playerCountThreadshold
           || !StringUtils.isBlank(serverOpenTimeFlagInfo.getCvalue())) {
            serverOpenTimeFlag = true;
            serverOpenTimeFlagInfo.setCvalue("true");
            configManager.update(serverOpenTimeFlagInfo);
        }
    }

    public boolean isGMEnabled() {
        return this.GMEnabled;
    }

    public boolean isEncryptEnabled() {
        return this.encryptEnabled;
    }

    public boolean isAuthEnabled() {
        return this.authEnabled;
    }

    public boolean isNeedCombine() {
        ServerInfo config = configManager.getConfig(ConfigKey.NEED_COMBINE);
        String value = config.getCvalue();
        return value != null && "true".equals(value);
    }

    public void resetNoNeedCombine() {
        Context.getBean(ConfigService.class).updateCombineTime(TimeUtils.timestamp());
        ServerInfo config = configManager.getConfig(ConfigKey.NEED_COMBINE);
        config.setCvalue("false");
        configManager.update(config);
//        ListenerManager.getInstance().fireServerCombine();
    }

    /**
     * 合服时间，时间戳，秒数
     * @return
     */
    public int getCombineTimestamp() {
        return combineTime;
    }

    public boolean isInWhiteList(String ip) {
        for (String pattern : patterns) {
            if (ip.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    public boolean isReinit() {
        return this.reinit;
    }

    public int getPort() {
        return port;
    }

    public String getAuthKey() {
        return authKey;
    }

    public int getOriServerId() {
        return oriServerId;
    }

    public boolean isPlatform(Platform platform) {
        return platform.getId() == getPlatformId();
    }

    public String getPlatform() {
        return platform;
    }

    public int getPlatformId() {
        return platformId;
    }

    public long getPlayerAutoIncId(int serverId) {
        return getAutoIncId(platformId, serverId);
    }

    public long getServerAutoIncId() {
        return getAutoIncId(platformId, oriServerId);
    }

    private long getAutoIncId(int platformId, int serverId) {
        return platformId * PLATFORM_OFFSET * SERVER_OFFSET + serverId * SERVER_OFFSET;
    }

    public boolean isPlatformMatch(String platform) {
        return org.apache.commons.lang.StringUtils.equals(platform, getPlatform());
    }

    public boolean isFlushStatus() {
        return flushStatus;
    }

    public boolean isServerHold(Integer serverId) {
        return this.holdingServerSet.contains(serverId);
    }

    public Set<Integer> getHoldingServerIds() {
        return Collections.unmodifiableSet(this.holdingServerSet);
    }

    public boolean isIDOverflow(int serverId, long incId) {
        return incId % SERVER_OFFSET >= SERVER_OFFSET - 1;
    }

    public boolean isPostIDOverflow(long incId) {
        return incId % (SERVER_OFFSET) >= SERVER_OFFSET - 1;
    }

    public boolean isIDOverflow(long incId) {
        return isIDOverflow(this.oriServerId, incId);
    }

    /**
     * 根据平台，服务器，时间戳生成唯一ID
     */
    public String getUUID() {
        StringBuilder sb = new StringBuilder(32);
        sb.append(this.getPlatform()).append('-');
        sb.append(this.getOriServerId()).append('-');
        sb.append(idInc.incrementAndGet());
        return sb.toString();
    }

    /**
     * 开服天数
     * @return
     */
    public final int getDayAfterOpenServer() {
        if (!isServerOpenTimeFlag()) {
            return 1;
        }
        long openServerMill = getOpenServerZeroTime();
        long nowMill = System.currentTimeMillis();
        int days = (int)((nowMill - openServerMill) / TimeUtils.TimeMillisOneDay) + 1;
        return days < 1 ? 1 : days;
    }

    public int getDayAfterCombine() {
        if (!isCombined()) {
            return 1;
        }
        long combineTime = this.combineTimeZeroTime;
        long nowMill = System.currentTimeMillis();
        int days = (int)((nowMill - combineTime) / TimeUtils.TimeMillisOneDay) + 1;
        return days < 1 ? 1 : days;
    }

    public long getOpenServerZeroTime() {
        return this.openServerZeroTime;
    }

    public boolean isServerOpen() {
        return serverOpen;
    }

    public void setServerOpen(boolean serverOpen) {
        this.serverOpen = serverOpen;
    }

    public boolean isServerOpenTimeFlag() {
        Preconditions.checkArgument(init, "未初始化");
        return serverOpenTimeFlag;
    }

    public boolean isCheckTimeChange() {
        return checkTimeChange;
    }

    @Override
    public void onCreate(PlayerActor playerActor, GameSession session) {
        int playerNum = playerService.incPlayerNum();
        if (!this.serverOpenTimeFlag && playerNum > playerCountThreadshold) {
            try {
                serverOpenTimeLock.lock();
                if (!this.serverOpenTimeFlag) {
                    int timestamp = TimeUtils.timestamp();
                    this.updateOpenTimestamp(timestamp);
                    this.serverOpenTimeFlag = true;
                    ServerInfo serverOpenTimeFlagInfo = configManager.getConfig(ConfigKey.OPEN_SERVER_TIME_FLAG);
                    serverOpenTimeFlagInfo.setCvalue("true");
                    configManager.update(serverOpenTimeFlagInfo);
//                    ListenerManager.getInstance().fireServerOpenTimeChanged(timestamp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                serverOpenTimeLock.unlock();;
            }
        }
    }

    public void gmResetOpenTime() {
        try {
            serverOpenTimeLock.lock();
            int timestamp = TimeUtils.timestamp();
            this.updateOpenTimestamp(timestamp);
            this.serverOpenTimeFlag = true;
            ServerInfo serverOpenTimeFlagInfo = configManager.getConfig(ConfigKey.OPEN_SERVER_TIME_FLAG);
            serverOpenTimeFlagInfo.setCvalue("true");
            configManager.update(serverOpenTimeFlagInfo);
//                    ListenerManager.getInstance().fireServerOpenTimeChanged(timestamp);
        } finally {
            serverOpenTimeLock.unlock();;
        }
    }

    public String getAdminKey() {
        return adminKey;
    }

    public int getIpLimitCount() {
        return ipLimitCount;
    }

    /**
     * 是否是IP玩家限制白名单
     */
    public boolean isIpLimitWhite(String ip) {
        for (String white : ipLimitWhiteList) {
            if (white.equals(ip)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSameServer(String platform, int serverId) {
        return StringUtils.equals(platform, this.platform) && (serverId == oriServerId || isServerHold(serverId));
    }
}
