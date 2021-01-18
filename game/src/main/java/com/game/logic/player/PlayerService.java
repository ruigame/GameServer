package com.game.logic.player;

import com.game.base.PlayerActor;
import com.game.base.UUIDService;
import com.game.file.ConfigPath;
import com.game.file.prop.PropConfigStore;
import com.game.logic.player.domain.Gender;
import com.game.logic.player.domain.RoleType;
import com.game.logic.player.entity.PlayerEntity;
import com.game.logic.player.manager.PlayerEntityManager;
import com.game.util.*;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
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

    private void loadAccount() {
        List<Object[]> accounts = this.playerEntityManager.getAllAccount();
        for (Object[] each : accounts) {
            String account = (String) each[0];
            Integer server = (Integer) each[1];
            Long playerId = (Long) each[2];
            Preconditions.checkArgument(registerAccount.putIfAbsent(account + "_" + server, playerId) == null,
                    "account:%s, server:%s", account, server);
        }
    }

    public void updateAccount(String key, long playerId) {
        this.registerAccount.put(key, playerId);
    }

    public boolean clearAccount2PlayerId(String key) {
        return registerAccount.remove(key, 0);
    }

    private void loadPlayerName() {
        Map<String, Long> allName = playerEntityManager.getAllName();
        this.playerNameMap = new ConcurrentHashMap<>(allName);
        this.playerNum = new AtomicInteger(playerNameMap.size());

        ConcurrentHashMap<Long, String> id2name = new ConcurrentHashMap<>();
        for (Map.Entry<String, Long> entry : allName.entrySet()) {
            id2name.put(entry.getValue(), entry.getKey());
        }
        this.playerId2Name = id2name;
    }

    public int getExistPlayerCount() {
        return playerNum.get();
    }

    public Collection<PlayerActor> getAllPlayerCache() {
        List<PlayerActor> playerActorList = new ArrayList<>();
        for (PlayerActor playerActor : this.id2PlayerActor.asMap().values()) {
            if (playerActor.isInit()) {
                playerActorList.add(playerActor);
            }
        }
        return playerActorList;
    }

    public String getNameByPlayerId(long playerId) {
        return playerId2Name.get(playerId);
    }

    public int incPlayerNum() {
        return playerNum.incrementAndGet();
    }

    @PostConstruct
    public void init() {
        this.loadPlayerName();;
        this.loadAccount();
    }

    public PlayerActor getPlayerActor(long playerId) {
        UseTimer useTimer = new UseTimer(playerId + " getPlayerActor", 500);
        PlayerActor player = getUnInitPlayerActor(playerId);
        if (!player.isInit()) {
            synchronized (player) {
                if (!player.isInit()) {
                    playerLoad(player);
                }
            }
        }
        useTimer.printUseTime();
        return player;
    }

    /**
     * 获取玩家对象（不初始化）
     * @param playerId
     * @return
     */
    public PlayerActor getUnInitPlayerActor(long playerId) {
        PlayerActor player = id2PlayerActor.getIfPresent(playerId);
        if (player == null) {
            player = new PlayerActor();
            player.setPlayerId(playerId);
            PlayerEntity playerEntity = playerEntityManager.getPlayerEntityById(player.getId());
            Preconditions.checkNotNull(playerEntity, "PlayerEntity NULL %s", player.getId());
            player.setPlayerEntity(playerEntity);
            player = cachePlayerActor(playerId, player);
        }
        return player;
    }

    public PlayerActor cachePlayerActor(long playerId, PlayerActor playerActor) {
        return ConcurrentUtils.putIfAbsent(id2PlayerActor.asMap(), playerId, playerActor);
    }

    /**
     * 玩家信息加载
     * @param playerActor
     */
    public void playerLoad(PlayerActor playerActor) {
        try {
//            ListenerManager.getInstance().firePlayerLoadListener(playerActor);
            playerActor.setInit(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 玩家登陆
     * @param playerActor
     */
    public void playerLogin(PlayerActor playerActor) {
//        if (!playerActor.isFightAttr()) {
//            playerActor.calculateFightAttr(false);
//        }
//        loginResetScene(playerActor); //重生登陆重置玩家到主城

    }
}
