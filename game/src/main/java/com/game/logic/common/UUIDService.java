package com.game.logic.common;

import com.game.base.exception.PlayerIdOverflowException;
import com.game.base.exception.ServerNotHoldedException;
import com.game.logic.guild.manager.GuildManager;
import com.game.logic.player.dao.PlayerEntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 游戏全局唯一ID
 * 用于合服和游戏内唯一索引
 * @Author: liguorui
 * @Date: 2020/9/20 11:03 下午
 */
@Component
public class UUIDService {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private PlayerEntityDao playerEntityDao;

    @Autowired
    private GuildManager guildManager;

    /**
     * 服务器标识到服务器当前最大玩家ID的对应
     */
    private Map<Integer, AtomicLong> server2maxId;

    private AtomicLong uniqueGuildId;

    public void initAll() {
        initializePlayerIds();
        initializeGuildId();
    }

    /**
     * 生成该服唯一的玩家Id
     * @param serverId
     * @return
     */
    public long makeUniquePlayerIdByServer(int serverId) {
        if (configService.isReinit()) {
            initializePlayerIds();
        }
        AtomicLong id = this.server2maxId.get(serverId);
        if (id == null) {
            throw new ServerNotHoldedException(serverId);
        }
        if (isIDOverflow(serverId, id.get())) {
            throw new PlayerIdOverflowException(id.get());
        }
        return id.incrementAndGet();
    }

    /**
     * 初始化玩家唯一ID
     */
    private void initializePlayerIds() {
        /**
         * 获取该数据库保存哪些服务器ID
         */
        List<Integer> serverIds = playerEntityDao.getAllServerIds();
        Map<Integer, Long> serverId2max = new HashMap<>(serverIds.size());
        for(Integer serverId : serverIds) {
            /**
             * 获取各个服务器ID下存在的最大玩家ID
             */
            Long maxId = playerEntityDao.getMaxIdByServerId(serverId);
            serverId2max.put(serverId, maxId);
        }

        Set<Integer> holdingServers = serverService.getHoldingServerSet();
        Map<Integer, AtomicLong> server2maxId = new HashMap<>(holdingServers.size());
        for (int serverId : holdingServers) {
            Long maxId = serverId2max.get(serverId);
            if (maxId == null || maxId == 0) {
                maxId = getStartPlayerIdByServerId(serverId);
            }
            server2maxId.put(serverId, new AtomicLong(maxId));
        }
        this.server2maxId = server2maxId;
    }

    /**
     * 根据服务器ID获取服务器上玩家的初始ID
     * @param serverId
     * @return
     */
    private long getStartPlayerIdByServerId(int serverId) {
        return configService.getPlayerAutoIncId(serverId);
    }

    private boolean isIDOverflow(int serverId, long incId) {
        return configService.isIDOverflow(serverId, incId);
    }

    private boolean isIDOverflow(long incId) {
        return configService.isIDOverflow(incId);
    }

    /**
     * 初始化公会唯一ID
     */
    public void initializeGuildId() {
        long maxId = 0;
        try {
            maxId = guildManager.getMaxGuildId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (maxId <= 0) {
            maxId = configService.getServerAutoIncId();
        }
        uniqueGuildId = new AtomicLong(maxId);
    }

    public final long makeUniqueGuildId() {
        if (configService.isReinit()) { //在内网，每次都查最大ID值
            initializeGuildId();
        }
        if (isIDOverflow(uniqueGuildId.get())) {
            throw new RuntimeException("makeUniqueGuildId error");
        }
        return uniqueGuildId.incrementAndGet();
    }
}
