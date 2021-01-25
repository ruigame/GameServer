package com.game.logic.common;

import com.game.PacketId;
import com.game.base.GameSessionHelper;
import com.game.base.ListenerHandler;
import com.game.base.PacketUtils;
import com.game.logic.RespLoginConflictPacket;
import com.game.logic.common.domain.IPlayerFilter;
import com.game.logic.desc.packet.resp.RespMessagePacket;
import com.game.logic.player.BaseService;
import com.game.logic.player.PlayerService;
import com.game.logic.player.domain.IPlayerId;
import com.game.net.CloseCause;
import com.game.net.packet.AbstractPacket;
import com.game.net.packet.PacketFactory;
import com.game.net.packet.Response;
import com.game.thread.gate.GateKeeper;
import com.game.thread.message.IMessage;
import com.game.util.Context;
import com.game.util.GameSession;
import com.game.util.GameSessionStatus;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liguorui
 * @Date: 2021/1/19 下午11:58
 */
@Component
public class OnlineService {

    private Logger logger = LoggerFactory.getLogger(OnlineService.class);
    private Logger onlineLogger = LoggerFactory.getLogger("onlineLogger");

    private int maxOnline = 0;

    @Autowired
    private BaseService baseService;

    /**
     * 账号 => 会话
     */
    private ConcurrentMap<String, GameSession> ACCOUNT_TO_SESSION = new ConcurrentHashMap<>();

    /**
     * 玩家id - 会话
     */
    private final ConcurrentMap<Long, GameSession> PLAYERID_TO_SESSION = new ConcurrentHashMap<>();

    private static final IPlayerFilter NONE_FILTER = new IPlayerFilter() {
        @Override
        public boolean isMatch(PlayerActor player) {
            return true;
        }
    };

    public boolean registerSession(final GameSession session, long playerId) {
        GameSession old = ACCOUNT_TO_SESSION.putIfAbsent(session.getAccount(), session);
        if (old != null) {
            if (old.getChannel().isActive()) {
                logger.info("sendLoginConflict {}", session.getAccount());
                PacketUtils.sendLoginConflict(old, session.getIp());
            } else {
                sessionClose(old);
            }
            return false;
        }
        session.setStatus(GameSessionStatus.REGISTER);
        PLAYERID_TO_SESSION.put(playerId, session);
        logger.debug("registerSession {}", session.getAccount());
        int current = PLAYERID_TO_SESSION.size();
        if (current > maxOnline) {
            maxOnline = current;
        }
        onlineLogger.info("current online:{}, max online:{}", current, maxOnline);
        new OnlineLogEvent(current, maxOnline).post();
        return true;
    }

    public GameSession getSession(String account) {
        return ACCOUNT_TO_SESSION.get(account);
    }

    public void removeSession(final GameSession session) {
        PLAYERID_TO_SESSION.remove(session.getPlayerId());
        ACCOUNT_TO_SESSION.remove(session.getAccount());
        onlineLogger.info("current online:{}, max online:{}", PLAYERID_TO_SESSION.size(), maxOnline);
    }

    public void sessionClose(final GameSession session) {
        synchronized (session) {
            if (!session.setExiting()) {
                return;
            }
        }
        logger.info("closeSession {}", session.getAccount());
        long playerId = session.getPlayerId();
        final PlayerActor player = Context.getBean(PlayerService.class).getPlayerActor(playerId);
        if (player != null) {
            player.addMessage(new IMessage<PlayerActor>() {
                @Override
                public void execute(PlayerActor p) {
                    ListenerHandler.getInstance().fireBeforePlayerLogoutListener(player);
                    player.logout();
                    ListenerHandler.getInstance().firePlayerLogoutListener(player);
                    removeSession(session);
                }
            });
        } else if (session.isRegister()) {
            removeSession(session);
        }
    }

    public void foreSessionClose(String account) {
        GameSession oldSession = getSession(account);
        if (oldSession == null) {
            return;
        }
        logger.warn("closeInactiveSession1 {}", oldSession.getAccount());
        final PlayerActor player = Context.getBean(PlayerService.class).getPlayerActor(oldSession.getPlayerId());
        if (player != null) {
            player.isDiscardReqPacet();
            player.addMessage(new IMessage<PlayerActor>() {
                @Override
                public void execute(PlayerActor p) {
                    if (player.isLoging()) {
                        ListenerHandler.getInstance().fireBeforePlayerLogoutListener(player);
                        player.logout();
                        ListenerHandler.getInstance().firePlayerLogoutListener(player);
                    }
                    removeSession(oldSession);
                }
            });
        } else {
            removeSession(oldSession);
        }
    }

    public void closeInactiveSession(final GameSession session) {
        if (session.isClose()) { //主动退出
            if (session.getCloseCause().isRelogin()) {
                baseService.putConnectInfo(session);
            }
            sessionClose(session);
            return;
        }
        baseService.putConnectInfo(session);
        //网络问题导致断线
        GateKeeper.schedule(session, "定时关闭不活跃的链接", new IMessage<GateKeeper>() {
            @Override
            public void execute(GateKeeper gateKeeper) {
                closeInactiveSession(session);
            }
        }, 30, TimeUnit.SECONDS);
    }

    private void closeInactiveSession0(final GameSession session) {
        synchronized (session) {
            if (session.isActive()) { //重连成功了
                return;
            }
            if (!session.setExiting()) {
                return;
            }
        }
        logger.debug("closeInactiveSession2 {}", session.getAccount());
        PlayerActor player = Context.getBean(PlayerService.class).getPlayerActorByPlayerId(session.getPlayerId());
        if (player != null) {
            player.addMessage(new IMessage<PlayerActor>() {
                @Override
                public void execute(PlayerActor p) {
                    ListenerHandler.getInstance().fireBeforePlayerLogoutListener(p);
                    p.logout();
                    ListenerHandler.getInstance().firePlayerLogoutListener(p);
                    removeSession(session);
                }
            });
        } else if (session.isRegister()) {
            removeSession(session);
        }
    }

    /**
     * 发送给在线所有人
     * @param packet
     */
    public void send2World(final AbstractPacket packet) {
        IPlayerFilter iPlayerFilter = NONE_FILTER;
        if (packet instanceof RespMessagePacket) {
            iPlayerFilter = new IPlayerFilter() { //不同聊天频道要玩家满足不同等级
                @Override
                public boolean isMatch(PlayerActor player) {
                    return player.getLevel() >0;
                }
            };
        }
        send2World(packet, iPlayerFilter);
    }

    /**
     * 发送给在线所有人，除了exculdes里面的
     * @param senderId
     * @param exculdes
     * @param packet
     */
    public void send2World(long senderId, Map<Long, Boolean> exculdes, final AbstractPacket packet) {
        IPlayerFilter iPlayerFilter = new IPlayerFilter() {
            @Override
            public boolean isMatch(PlayerActor player) {
                if (exculdes == null || !exculdes.containsKey(player.getId())) {
                    return true;
                }
                return false;
            }
        };
        send2World(packet, iPlayerFilter);
    }

    /**
     * 发送给在线所有人，除了filter返回false之外
     * @param packet
     * @param filter
     */
    public void send2World(AbstractPacket packet, IPlayerFilter filter) {
        Response response = packet.write();
        send2World(response, filter);
    }

    public void send2World(Response response, IPlayerFilter filter) {
        for (GameSession session : ACCOUNT_TO_SESSION.values()) {
            long playerId = session.getPlayerId();
            PlayerActor player = Context.getBean(PlayerService.class).getPlayerActor(playerId);
            if (player != null) {
                if (filter == null || filter.isMatch(player)) {
                    try {
                        ReferenceCountUtil.retain(response);
                        if (!GameSessionHelper.write(response, session)) {
                            ReferenceCountUtil.release(response);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ReferenceCountUtil.release(response);
                    }
                }
            }
        }
        ReferenceCountUtil.release(response);
    }

    /**
     * 发送给单个玩家
     * @param packet
     * @param id
     * @return
     */
    public boolean send2Player(AbstractPacket packet, long id) {
        PlayerActor player = getOnlinePlayer(id);
        if (player == null) {
            return false;
        }

        player.sendPacket(packet);
        return true;
    }

    /**
     * 发送给某些玩家
     * @param packet
     * @param iPlayerIds
     */
    public void send2PlayerIds(AbstractPacket packet, Collection<? extends IPlayerId> iPlayerIds) {
        Response response = packet.write();
        for (IPlayerId iPlayerId : iPlayerIds) {
            try {
                ReferenceCountUtil.retain(response);
                if (!send2Player(packet, iPlayerId.getPlayerId())) {
                    ReferenceCountUtil.release(response);
                }
            } catch (Exception e) {
                ReferenceCountUtil.release(response);
            }
        }
        ReferenceCountUtil.release(response);
    }

    /**
     * 返回全部在线玩家
     * @return
     */
    public List<PlayerActor> getAllOnlinePlayer() {
        List<PlayerActor> list = new ArrayList<>(ACCOUNT_TO_SESSION.size());
        for (GameSession session : ACCOUNT_TO_SESSION.values()) {
            long playerId = session.getPlayerId();
            PlayerActor playerActor = Context.getBean(PlayerService.class).getPlayerActorByPlayerId(playerId);
            if (playerActor != null) {
                list.add(playerActor);
            }
        }
        return list;
    }

    public Set<Long> getAllOnlinePlayerIds() {
        Set<Long> playerIds = new HashSet<>();
        for (GameSession session : ACCOUNT_TO_SESSION.values()) {
            long playerId = session.getPlayerId();
            PlayerActor playerActor = Context.getBean(PlayerService.class).getPlayerActorByPlayerId(playerId);
            if (playerActor != null) {
                playerIds.add(playerId);
            }
        }
        return playerIds;
    }

    /**
     * 根据玩家id获取在线玩家
     * @param playerId
     * @return
     */
    public PlayerActor getOnlinePlayer(long playerId) {
        GameSession session = PLAYERID_TO_SESSION.get(playerId);
        if (session == null) return null;
        PlayerActor player = Context.getBean(PlayerService.class).getPlayerActorByPlayerId(playerId);
        return player;
    }

    /**
     * 玩家是否在线
     */
    public boolean isOnline(long playerId) {
        return PLAYERID_TO_SESSION.containsKey(playerId);
    }

    /**
     * 获取在线玩家数量
     * @return
     */
    public int getOnlinePlayerCount() {
        return PLAYERID_TO_SESSION.size();
    }

    public void closeAll() {
        for (GameSession session : PLAYERID_TO_SESSION.values()) {
            RespLoginConflictPacket packet = PacketFactory.createPacket(PacketId.Utils.RESP_LOGIN_CONFLICT);
            packet.maintain();
            Response response = packet.write();
            GameSessionHelper.writeAndClose(session, response, CloseCause.MAINTAIN);
        }
    }

    public int getIpPlayerCount(String ip) {
        int count = 0;
        for (GameSession session : PLAYERID_TO_SESSION.values()) {
            if (ip.equals(session.getIp())) {
                count ++;
            }
        }
        return count;
    }

    public void send2World(Response response) {
        send2World(response, null);
    }
}
