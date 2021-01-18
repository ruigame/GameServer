package com.game.logic.player;

import com.game.base.PlayerActor;
import com.game.base.UUIDService;
import com.game.file.ConfigPath;
import com.game.file.prop.PropConfigStore;
import com.game.logic.player.domain.Gender;
import com.game.logic.player.domain.RoleType;
import com.game.logic.player.entity.PlayerEntity;
import com.game.logic.player.manager.PlayerEntityManager;
import com.game.util.ConcurrentHashSet;
import com.game.util.GameSession;
import com.game.util.Listener;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 5:54 下午
 */
@Component
@Listener(order = Listener.MIN_PRIORITY)
public class PlayerService {

    private static final Logger LOGGER = LoggerFactory.getLogger("playerService");

    @Autowired
    private PlayerEntityManager playerEntityManager;

    @Autowired
    private UUIDService uuidService;

    /**
     * 玩家名字 - 玩家id
     */
    private ConcurrentHashMap<String ,Long> playerNameMap = new ConcurrentHashMap<>();

    /**
     * 玩家id - 玩家名字
     */
    private ConcurrentHashMap<Long, String> playerId2Name = new ConcurrentHashMap<>();

    /**
     * 非机器人玩家数据
     */
    private AtomicInteger playerNum = new AtomicInteger();

    /**
     * 已存在的账号（账号已经转小写）key为account_server
     */
    private ConcurrentHashMap<String ,Long> registerAccount = new ConcurrentHashMap<>();

    /**
     * 玩家对象缓存
     */
    private Cache<Long, PlayerActor> id2PlayerActor = CacheBuilder.newBuilder().softValues().build();

    private Set<Long> loginPlayers = new ConcurrentHashSet<>();

    /**
     * 是否还能创角
     */
    private boolean checkThreadSafe = PropConfigStore.getPropConfig(ConfigPath.DEBUG_PROPERTIES).getBoolean("CHECK_THREAD_SAFE");

    public PlayerEntity createPlayer(GameSession session, int serverId, String account, String name, RoleType roleType, Gender gender) {
        return createPlayer(session, serverId, account, name, roleType, gender, true);
    }

    /**
     * 新建一个角色
     * @return
     */
    private PlayerEntity createPlayer(GameSession session, int serverId, String account, String name,
                                      RoleType roleType, Gender gender, boolean persistent) {
        long playerId = this.uuidService.makeUniquePlayerIdByServer(serverId);
        PlayerEntity playerEntity = this.playerEntityManager.createPlayerEntity(session, serverId, account,
                                                playerId, name, roleType, gender, persistent);
        return playerEntity;
    }

    /**
     * 注册临时名称，在成功创建时在更新ID
     * @param name
     * @return
     */
    public boolean registerTemporaryNickName(String name) {
        return playerNameMap.putIfAbsent(name, Long.valueOf(0)) == null;
    }

    /**
     * 名称是否已经存在
     * @param name
     * @return
     */
    public boolean isNameExist(String name) {
        return playerNameMap.containsKey(name);
    }

    /**
     * 将原来注册的名称对应id改成正确的
     * @param name
     * @param id
     * @return
     */
    public boolean updateTemporaryNickName(String name, long id) {
        boolean success = playerNameMap.replace(name, 0L, id);
        if (success) {
            playerId2Name.put(id, name);
        }
        return success;
    }

    public void unregisterNickName(String nickName) {
        Long playerId = playerNameMap.remove(nickName);
        if (playerId != null) {
            playerId2Name.remove(playerId);
        }
    }

    public void removeOldName(String name) {
        playerNameMap.remove(name);
    }

    public boolean isExistPlayer(long playerId) {
        return playerId2Name.containsKey(playerId);
    }

    public long getPlayerIdByName(String name) {
        if (!playerNameMap.containsKey(name)) {
            return -1;
        }
        return playerNameMap.get(name);
    }

    //=============初始化相关============
}
