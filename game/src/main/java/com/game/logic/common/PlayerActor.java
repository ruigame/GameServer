package com.game.logic.common;

import com.game.base.IPlayer;
import com.game.base.handler.PacketMessage;
import com.game.logic.guild.entity.Guild;
import com.game.logic.guild.manager.GuildManager;
import com.game.logic.player.domain.PlayerResourceType;
import com.game.logic.player.domain.PlayerStatus;
import com.game.logic.player.domain.ResourceType;
import com.game.logic.player.domain.Role;
import com.game.logic.player.entity.PlayerEntity;
import com.game.net.LoginAuthParam;
import com.game.net.packet.AbstractPacket;
import com.game.thread.message.IMessage;
import com.game.thread.message.MessageHandler;
import com.game.util.Context;
import com.game.util.ExceptionUtils;
import com.game.util.GameSession;
import com.game.util.TimeUtils;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午4:59
 */
public class PlayerActor extends MessageHandler<PlayerActor> implements IPlayer<PlayerActor> {

    private Logger logger = LoggerFactory.getLogger(PlayerActor.class);

    private PlayerEntity playerEntity;

    private GameSession session;

    private long playerId;

    /**
     * 是否登陆
     */
    private AtomicBoolean isLogin = new AtomicBoolean(false);

    /**
     * 是否丢弃请求包
     */
    private volatile  boolean discardReqPacet;

    private AtomicBoolean calFightAttrGate = new AtomicBoolean();

    /**
     * 初始化加载模块数据
     */
    private volatile boolean init;

    /**
     * 玩家状态
     */
    private PlayerStatus status;

    public PlayerActor(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public PlayerActor() {
    }

    public String getAccount() {
        return playerEntity.getAccount();
    }

    public String getServer() {
        return String.valueOf(playerEntity.getServerId());
    }

    public boolean isLogined() {
        return isLogin.get();
    }

    @Override
    public String getName() {
        return playerEntity.getName();
    }

    public String getGid() {
        GameSession session = getSession();
        if (session != null) {
            LoginAuthParam loginAuthParam = session.getLoginAuthParam();
            if (loginAuthParam != null) {
                return loginAuthParam.getGid();
            }
        }
        return null;
    }

    public String getParam() {
        return session != null ? session.getParam() : "";
    }

    public String getPid() {
        GameSession session = getSession();
        if (session != null) {
            LoginAuthParam loginAuthParam = session.getLoginAuthParam();
            if (loginAuthParam != null) {
                return loginAuthParam.getPid();
            }
        }
        return null;
    }

    @Override
    public long getPlayerId() {
        return playerId;
    }

    @Override
    public void sendPacket(AbstractPacket packet) {

    }

    @Override
    public void tryEnterScene() {

    }

    public boolean isDiscardReqPacet() {
        return discardReqPacet;
    }

    public void discardReqPacket() {
        this.discardReqPacet = true;
    }

    public void setDiscardReqPacet(boolean discardReqPacet) {
        this.discardReqPacet = discardReqPacet;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isInitFightAttr() {
        return !getRole2FightAttrMap().isEmpty() && getRole2FightAttrMap().size() == getRoleSize();
    }

    public Map<Integer, Map<ResourceType, Long>> getRole2FightAttrMap() {
        return this.playerEntity.getRole2FightAttrMap();
    }

    public int getRoleSize() {
        return getRoleList().size();
    }

    public List<Role> getRoleList() {
        return playerEntity.getRoleList();
    }

    public void calculateFightAttr() {
        if (calFightAttrGate.compareAndSet(false, true)) {
            schedule("计算战力", new IMessage<PlayerActor>() {
                @Override
                public void execute(PlayerActor p) {
                    calFightAttrGate.set(false);
                    calculateFightAttr(true);
                }
            }, 500, TimeUnit.MILLISECONDS);
        }
    }

    public void calculateFightAttr(boolean send) {
        if (!isInThread()) {
            ExceptionUtils.log("计算战力不在玩家线程");
            addMessage(new IMessage<PlayerActor>() {
                @Override
                public void execute(PlayerActor p) {
                    clearAttrAndFightingCache();
//                    Context.getRoleFightAttrService().processFightAttr(this, send);
                }
            });
            return;
        }
        clearAttrAndFightingCache();
//                    Context.getRoleFightAttrService().processFightAttr(this, send);
    }

    public void clearAttrAndFightingCache() {
//        attrCache.clear();
//        fightingCache.clear();
//        sysFacAttrCache.clear();
    }

    public void login() {
        isLogin.compareAndSet(false, true);
        if (getLastZeroTime() == 0) {
            updateLastZeroTime();
        }
        playerEntity.login(getIp());
//        Context.getBean(MidNightService.class).playerOnZero(this);
        discardReqPacet = false;
    }

    public void logout() {
        if (isLogin.compareAndSet(true, false)) {
            logger.debug("logout {}", getAccount());
            playerEntity.logout();
            changeStatus(null);
            cancleAllSchedule();
            calFightAttrGate.set(false);
//            if (fireFightingChange) {//调度还没执行
//                imFireChangeFight();
//            }
        }
    }

    /**
     * 上次零点到当前时间过去的天数
     * @return
     */
    public int passZeroTime() {
        long lastZeroTime = getLastZeroTime();
        return TimeUtils.getNDayFromTimes(lastZeroTime);
    }

    public void changeStatus(PlayerStatus newStatus) {
        if (status == newStatus) {
            return;
        }
        logger.info("{} change status from {} to {}", getAccount(), status, newStatus);
        if (status != null) {
            status.out(this);
        }
        status = newStatus;
        if (status != null) {
            status.in(this);
        }
    }

    public boolean isLoging() {
        return isLogin.get();
    }

    public long getLastZeroTime() {
        return this.playerEntity.getLastZeroTime();
    }

    public void updateLastZeroTime() {
        this.playerEntity.updateLastZeroTime();
    }

    public String getIp() {
        return session.getIp();
    }

    public int getLevel() {
        return playerEntity.getLevel();
    }

    public long getGuildId() {
        return playerEntity.getGuildId();
    }

    public Guild getGuild() {
        long guildId = this.getGuildId();
        if (guildId > 0) {
            return Context.getBean(GuildManager.class).getGuild(guildId);
        }
        return null;
    }

    public Map<ResourceType, Long> getFightAttrMap(int roleIndex) {
        return getRole2FightAttrMap().getOrDefault(roleIndex, Collections.emptyMap());
    }

    public long getRoleFighting(int roleIndex) {
        return getRoleByIndex(roleIndex).getFighting();
    }

    public Role getRoleByIndex(int roleIndex) {
        return playerEntity.getRoleByIndex(roleIndex);
    }

    public Map<PlayerResourceType, Long> getAllRes() {
        Map<PlayerResourceType, Long> map = Maps.newHashMap();
        for (PlayerResourceType each : PlayerResourceType.values()) {
            map.put(each, each.getValue(this));
        }
        return map;
    }

    public String getGuildName() {
        Guild guild = getGuild();
        return guild != null ? guild.getName() : null;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getId() {
        return playerId;
    }

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    public void setPlayerEntity(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public GameSession getSession() {
        return session;
    }

    public void setSession(GameSession session) {
        this.session = session;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    /**
     * 打印前几个协议情况
     * @return
     */
    public String printTopPacket() {
        Map<Short, AtomicInteger> packet2Nummap = new HashMap<>(getMessageSize());
        for (IMessage<PlayerActor> message : getMessages()) {
            if (!(message instanceof PacketMessage)) {
                continue;
            }
            PacketMessage packetMessage = (PacketMessage)message;
            packet2Nummap.computeIfAbsent(packetMessage.getPacket().getCmd(), c -> new AtomicInteger()).incrementAndGet();
        }

        List<Map.Entry<Short, AtomicInteger>> messageNumList = new ArrayList<>();
        messageNumList.addAll(packet2Nummap.entrySet());
        messageNumList.sort((m1, m2) -> Integer.compare(m2.getValue().get(), m2.getValue().get()));
        StringBuilder builder = new StringBuilder(128);
        for (int i= 0,length = messageNumList.size(); i < 5 && i < length; i++) {
            Map.Entry<Short, AtomicInteger> entry = messageNumList.get(i);
            if (i > 0) {
                builder.append(',');
            }
            builder.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return builder.toString();
    }

}
