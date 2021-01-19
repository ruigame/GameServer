package com.game.logic.player;

import com.game.PacketId;
import com.game.base.PlayerActor;
import com.game.base.UUIDService;
import com.game.file.ConfigPath;
import com.game.file.prop.PropConfigStore;
import com.game.logic.player.domain.*;
import com.game.logic.player.entity.PlayerEntity;
import com.game.logic.player.manager.PlayerEntityManager;
import com.game.logic.player.packet.resp.RespMainRolePacket;
import com.game.logic.player.packet.resp.RespRoleCreatePacket;
import com.game.net.packet.PacketFactory;
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
        if (!playerActor.isInitFightAttr()) {
            playerActor.calculateFightAttr(false);
        }
        loginResetScene(playerActor); //重生登陆重置玩家到主城
        PlayerInfoVo playerInfoVo = generatePlayerInfo(playerActor);
        RespMainRolePacket packet = PacketFactory.createPacket(PacketId.Role.RESP_MAIN_ROLE);
        packet.initBaseInfo(playerInfoVo);
        playerActor.sendPacket(packet);
        loginPlayers.add(playerActor.getId());

        playerActor.login();
//        ListenerManager.getInstance().firePlayerLoginListener(playerActor);
        playerActor.calculateFightAttr(); //不应该在这里调用，但就系统都在登陆监听了调用，去掉，统一放在这里简单点省去每个地方listener都调用
    }

    /**
     * 登陆检查重置玩家所在的场景
     * @param playerActor
     */
    private void loginResetScene(PlayerActor playerActor) {
        playerActor.getPlayerEntity().setSceneId(getSceneId(playerActor));
    }

    private int getSceneId(PlayerActor playerActor) {
        return -1;
    }

    public PlayerInfoVo generatePlayerInfo(PlayerActor player) {
        PlayerInfoVo playerInfoVo = new PlayerInfoVo();
        playerInfoVo.setPlayerId(player.getId());
        playerInfoVo.setName(player.getName());
        playerInfoVo.setGuildId(player.getGuildId());
        playerInfoVo.setGuildName(player.getGuildName());
        playerInfoVo.setSceneId(player.getPlayerEntity().getSceneId());
        List<Role> roleList = player.getRoleList();
        List<RoleInfoVo> roleInfos = new ArrayList<>();
        for (Role each : roleList) {
            RoleInfoVo roleInfoVo = generateRoleInfo(player, each);
            roleInfos.add(roleInfoVo);
        }
        playerInfoVo.setRoleInfos(roleInfos);
        playerInfoVo.setResMap(player.getAllRes());
        playerInfoVo.setConnectKey(player.getSession().getConnectKey());
        return playerInfoVo;
    }

    public RoleInfoVo generateRoleInfo(PlayerActor player, Role role) {
        return generateRoleInfo(player, role, true);
    }

    public RoleInfoVo generateRoleInfo(PlayerActor player, Role role, boolean containFight) {
        RoleInfoVo roleInfoVo = new RoleInfoVo();
        int index = role.getIndex();
        roleInfoVo.setIndex(index);
        roleInfoVo.setRoleType(role.getRoleType());
        roleInfoVo.setGender(role.getGender());
        roleInfoVo.setAvatar(role.getAvatar());
        roleInfoVo.setWeapon(role.getWeapon());
        Map<ResourceType, Long> attrs = player.getFightAttrMap(index);
        roleInfoVo.setAttrs(attrs);
        roleInfoVo.setFighting(containFight ? player.getRoleFighting(role.getIndex()) : 0);
        return roleInfoVo;
    }

    /**
     * 创建新角色
     * @param playerActor
     * @param roleType
     * @param gender
     * @return
     */
    public boolean createNewRole(PlayerActor playerActor, RoleType roleType, Gender gender) {
        PlayerEntity playerEntity = playerActor.getPlayerEntity();
        List<Role> roles = playerEntity.getRoleList();
        if (roles.size() >= 3) {
            return false;
        }
        if (playerEntity.isRoleTypeExist(roleType)) {
            return false;
        }
        int index = roles.size() - 1;
        Role newRole = new Role(index, roleType, gender);
        playerEntity.addRole(newRole);
        playerEntity.immediateUpdate();

        RespRoleCreatePacket packet = PacketFactory.createPacket(PacketId.Role.RESP_ROLE_CREATE);
        RoleInfoVo roleInfoVo = generateRoleInfo(playerActor, newRole);
        packet.init(roleInfoVo);
        playerActor.sendPacket(packet);

//        ListenerManager.getInstance().fireRoleCreateListener(playerActor, newRole);
        playerActor.calculateFightAttr();
        return true;
    }

    public PlayerActor getPlayerActorByName(String name) {
        Long playerId = playerNameMap.get(name);
        if (playerId == null) {
            return null;
        }
        return getPlayerActor(playerId);
    }

    public Long registerTemporaryAccount(String account2server) {
        return registerAccount.putIfAbsent(account2server, 0L);
    }

    public Long getPlayerIdByASKey(String account2server) {
        return registerAccount.get(account2server);
    }

    public PlayerActor getPlayerByASKey(String account2server) {
        Long playerId = getPlayerIdByASKey(account2server);
        if (playerId != null) {
            return getPlayerActor(playerId);
        }
        return null;
    }

    public void checkThreadSafe(long playerId) {
        if (!checkThreadSafe) {
            return;
        }
        PlayerActor playerActor = getPlayerActor(playerId);
        if (!playerActor.isInThread()) {
            ExceptionUtils.log("checkThreadSafe no inThread");
        }
    }

    public void checkPlayer() {
        Map<String, PlayerEntity> name2player = new HashMap<>();
        List<PlayerEntity> p1 = this.playerEntityManager.getDuplicateName();
        for (PlayerEntity p : p1) {
            PlayerEntity old = name2player.get(p.getName());
            if (old != null) {
                PlayerEntity temp = null;
                PlayerEntity std = null;
                PlayerNameCompare curP = new PlayerNameCompare(p);
                PlayerNameCompare oldP = new PlayerNameCompare(old);
                if (curP.isNameMore(oldP)) {
                    temp = old;
                    std = p;
                } else {
                    temp = p;
                    std = old;
                }
                if (!processPlayerRename(temp)) {
                    throw new RuntimeException("can not rename" + temp);
                }
                name2player.put(std.getName(), std);
            } else {
                name2player.put(p.getName(), p);
            }
        }
        loadPlayerName();
    }

    private boolean processPlayerRename(PlayerEntity p) {
        String oldName = p.getName();
        int retryTimes = 100;
        int index = 0;
        for (int i = 0; i < retryTimes; i++) {
            String newName = oldName + "-[" + p.getServerId() + "]";
            if (index > 0) {
                newName = newName + "." + index;
            }
            if (registerTemporaryNickName(newName)) {
                updateTemporaryNickName(newName, p.getPlayerId());
                p.changeName(newName);
                LOGGER.info("ChangePlayerName:{} {}", p.getPlayerId(), newName);
                Context.getBean(PlayerEntityManager.class).update(p);
//                ListenerManager.getInstance().firePlayerRename(p);
                //发邮件
                return true;
            }
            index ++;
        }
        return false;
    }
}
