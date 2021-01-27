package com.game.logic.player;

import com.game.PacketId;
import com.game.base.GameSessionHelper;
import com.game.base.ListenerHandler;
import com.game.base.PacketUtils;
import com.game.base.exception.PlayerIdOverflowException;
import com.game.base.exception.ServerNotHoldedException;
import com.game.logic.common.*;
import com.game.logic.common.manger.ConfigRandomNameManger;
import com.game.logic.common.packet.req.ReqMessageVersionPacket;
import com.game.logic.common.packet.resp.RespMessageVersion;
import com.game.logic.player.domain.ConnectInfo;
import com.game.logic.player.domain.Gender;
import com.game.logic.player.domain.RoleType;
import com.game.logic.player.entity.PlayerEntity;
import com.game.logic.player.log.CreatePlayerLogEvent;
import com.game.logic.player.log.RegisterLogEvent;
import com.game.logic.player.packet.req.*;
import com.game.logic.player.packet.resp.*;
import com.game.net.ChannelUtils;
import com.game.net.CloseCause;
import com.game.net.LoginAuthParam;
import com.game.net.packet.PacketFactory;
import com.game.thread.gate.GateKeeper;
import com.game.thread.message.IMessage;
import com.game.util.*;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.channel.Channel;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liguorui
 * @Date: 2021/1/20 上午12:01
 */
@Component
public class BaseService implements IBaseService, ServerStarter{

    private Logger logger = LoggerFactory.getLogger("base");

    @Autowired
    private WordService wordService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private OnlineService onlineService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ForbidService forbidService;

    @Autowired
    private ConfigRandomNameManger configRandomNameManger;

    private Cache<String, ConnectInfo> asKey2ConnectInfoCache = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.MINUTES).build();

    private String messageVersion = "0";

    @Override
    public void reqMessageVersion(GameSession session, ReqMessageVersionPacket req) {
        GameSessionHelper.sendPacket(session, RespMessageVersion.create(messageVersion));
    }

    @Override
    public void loginAuth(GameSession session, ReqLoginAuthPacket packet) {
        LoginAuthParam loginAuthParam = packet.getLoginAuthParam();
        logger.info("LoginAuth {}, ip:{}", loginAuthParam, session.getIp());
        if (!session.compareAndSetStatus(GameSessionStatus.INIT, GameSessionStatus.AUTHING)) {
            return;
        }

        int ipPlayerCount;
        String ip = session.getIp();
        //每个ip限制登陆人数
        if (!configService.isIpLimitWhite(ip) && (ipPlayerCount = onlineService.getIpPlayerCount(ip)) >= configService.getIpLimitCount()) {
            sendAndClose(session, RespLoginAuthPacket.IP_COUNT_LIMIT);
            logger.warn("{} {} {} ipLimit", session.getAccount(), session.getIp(), ipPlayerCount);
            return;
        }

        if (!checkAuth(loginAuthParam)) {
            sendAndClose(session, RespLoginAuthPacket.STATUS_FAIL);
            return;
        }

        session.setLoginAuthParam(loginAuthParam);
        session.compareAndSetStatus(GameSessionStatus.AUTHING, GameSessionStatus.AUTHED);

        Long playerId = playerService.registerTemporaryAccount(session.getASKey());
        if (playerId == null) {
            //新注册账号 记录日志
            new RegisterLogEvent(configService.getPlatform(), configService.getOriServerId(), loginAuthParam.getAccount(),
                    0, "", loginAuthParam.getPid(), loginAuthParam.getGid(), loginAuthParam.getParam()).post();
        }

        RespLoginAuthPacket respPacket = PacketFactory.createPacket(PacketId.Base.RESP_LOGIN_AUTH);
        respPacket.success();
        if (playerId != null) {
            respPacket.setHasPlayer(true);
        }
        GameSessionHelper.sendPacket(session, respPacket);
    }

    private boolean checkAuth(LoginAuthParam loginAuthParam) {
        if (StringUtils.isBlank(loginAuthParam.getAccount())) {
            logger.warn("account is null");
            return false;
        }

        if (!configService.isAuthEnabled()) {
            return true;
        }

        if (StringUtils.isBlank(loginAuthParam.getPlatform())) {
            logger.warn("platform is null");
            return false;
        }

        if (loginAuthParam.getServerId() <= 0) {
            logger.warn("serverId is null");
            return false;
        }

        if (!configService.isServerHold(loginAuthParam.getServerId())) {
            logger.warn("serverId: {} not hold!", loginAuthParam.getServerId());
            return false;
        }

        int timestamp = loginAuthParam.getTime();
        if (timestamp < 1) {
            logger.warn("serverId: {} not hold!", loginAuthParam.getServerId());
            return false;
        }

        if (Math.abs(timestamp - TimeUtils.timestamp()) > TimeUtils.SecondsTenMinute) {
            logger.warn("param time diff too long curTime:{}, paramTime:{}", TimeUtils.timestamp(), loginAuthParam.getTime());
            return false;
        }

        StringBuilder sb = this.initContent(loginAuthParam);
        sb.append(configService.getAuthKey());
        String expectSign = DigestUtils.md5Hex(sb.toString());
        if (!expectSign.equals(loginAuthParam.getSign())) { //md5加密码验证
            logger.warn(sb.toString());
            logger.warn("param sign not match !{} {}", loginAuthParam.getSign(), expectSign);
            return false;
        }
        return true;
    }

    private StringBuilder initContent(LoginAuthParam loginAuthParam) {
        StringBuilder temp = new StringBuilder(128)
                            .append(loginAuthParam.getGid())
                            .append(loginAuthParam.getPid())
                            .append(loginAuthParam.getServerId())
                            .append(loginAuthParam.getAccount())
                            .append(loginAuthParam.getTime());
        return temp;
    }

    private void sendAndClose(GameSession session, byte status) {
        RespLoginAuthPacket respPacket = PacketFactory.createPacket(PacketId.Base.RESP_LOGIN_AUTH);
        respPacket.setStatus(status);
        PacketUtils.sendAndClose(session, respPacket);
    }

    public void handlePlayerLogin(final GameSession session) {
        handlePlayerLogin0(session, 1);
    }

    private void handlePlayerLogin0(final GameSession session, final int times) {
        UseTimer useTimer = new UseTimer(session.getAccount() + "handlePlayerLogin0", 800);
        logger.debug("handlePlayerLogin0 {}", session.getAccount());
        final long playerId = playerService.getPlayerIdByASKey(session.getASKey());
        if (playerId <= 0) {
            logger.warn("{} {} playerId is invalid", session.getServerId(), session.getAccount());
            session.close(CloseCause.Illegal_Operation);
            return;
        }

        PlayerActor player = playerService.getPlayerActor(playerId);
        useTimer.printUseTime("11");

        //是否被封禁
        if (!session.isBackend()) {
            boolean fobid = forbidService.isRoleForbided(player);
            boolean fobidIp = forbidService.isIPBan(session.getIp());
            if (fobidIp) {
                RespForbidedMessagePacket forbidIPpacket = forbidService.createRoleForbidedIPPacket(player, session.getIp());
                if (forbidIPpacket != null) {
                    PacketUtils.sendAndClose(session, forbidIPpacket);
                    return;
                }
            }
            if (fobid) {
                RespForbidedMessagePacket forbidpacket = forbidService.createRoleForbidedPacket(player);
                if (forbidpacket != null) {
                    PacketUtils.sendAndClose(session, forbidpacket);
                    return;
                }
            }
        }

        useTimer.printUseTime("22");
        if (!onlineService.registerSession(session, playerId)) {
            if (times >= 5) {
                ExceptionUtils.log("{}尝试{}次登陆异常***,玩家当前正在处理的消息:{}, 协议：{}", session.getAccount(),
                        times, player.printTopMessage(), player.printTopPacket());
                onlineService.foreSessionClose(session.getAccount());
                return;
            }
            GateKeeper.schedule(session, "TryRegisterSession", new IMessage<GateKeeper>() {
                @Override
                public void execute(GateKeeper k) {
                    handlePlayerLogin0(session, times + 1);
                }
            }, 500, TimeUnit.MILLISECONDS);
            return;
        }

        session.produceReconnectKey();
        player.addMessage(new IMessage<PlayerActor>() {
            @Override
            public void execute(PlayerActor finalPlayer) {
                session.setPlayerId(finalPlayer.getId());
//                playerService.cachePlayerActor(finalPlayer); //要解决强引用
                finalPlayer.setSession(session);
                playerService.playerLogin(finalPlayer);
                useTimer.printUseTime("44");
            }
        });
        useTimer.printUseTime("33");
    }

    public void putConnectInfo(GameSession session) {
        asKey2ConnectInfoCache.put(genKey(session.getAccount(),
                session.getServerId()), new ConnectInfo(session.getLoginAuthParam(), session.getConnectKey(), session.getRespSN()));
    }

    private String genKey(String account, int serverId) {
        return account + "_" + serverId;
    }

    @Override
    public void randomName(GameSession session, ReqNameRandomPacket packet) {
        String name = getRandomName(packet.getGender());
        if (name != null) {
            RespNameRandomPacket resp = PacketFactory.createPacket(PacketId.Base.RESP_NAME_RANDOM);
            resp.init(name);
            GameSessionHelper.sendPacket(session, resp);
        }
    }

    @Override
    public void roleCreate(GameSession session, ReqPlayerCreatePacket packet) {
        Long playerId = playerService.getPlayerIdByASKey(session.getASKey());
        Preconditions.checkNotNull(playerId, "ASKey:%s, account:%s, ip:%s", session.getASKey(), session.getAccount(), session.getIp());
        if (playerId > 0) {
            return;
        }
        doRoleCreate(session, packet);
    }

    public void doRoleCreate(GameSession session, ReqPlayerCreatePacket req) {
        String nickName = req.getNickName();
        nickName = StringUtils.trim(nickName);

        byte roleTypeByte = req.getRoleType();
        byte genderByte = req.getGender();
        RoleType roleType = RoleType.getById(roleTypeByte);
        Gender gender = Gender.getById(genderByte);
        nickName = StringUtils.trim(nickName);

        RespRoleCreateResultPacket resp = PacketFactory.createPacket(PacketId.Base.RESP_CREATE_PLAYER);

        if (roleType == null) {
            resp.fail4NotRoleType();
            GameSessionHelper.sendPacket(session, resp);
            return;
        }

        if (gender == null) {
            resp.fail4NotGender();
            GameSessionHelper.sendPacket(session, resp);
            return;
        }

        boolean resultState = wordService.isAvailableFormatForCreate(nickName);
        if (!resultState) {
            resp.fail4NameFormat();
            GameSessionHelper.sendPacket(session, resp);
            return;
        }

        //达到创角限制
        if (configService.getDayAfterOpenServer() >= 10) {
            resp.fail4PlayerCreateLimit();
            GameSessionHelper.sendPacket(session, resp);
            return;
        }

        if (roleType == RoleType.None) {
            resp.fail4InvalidRoleType();
            GameSessionHelper.sendPacket(session, resp);
            return;
        }

        if (!playerService.registerTemporaryNickName(nickName)) {
            resp.fail4NameExists();
            GameSessionHelper.sendPacket(session, resp);
            return;
        }

        String asKey = session.getASKey();
        try {
            String account = session.getAccount();
            int serverId = session.getServerId();
            PlayerEntity playerEntity = playerService.createPlayer(session, serverId, account, nickName, roleType, gender);
            if (playerEntity != null) {
                playerService.updateTemporaryNickName(nickName, playerEntity.getPlayerId());
                playerService.updateAccount(asKey, playerEntity.getPlayerId());
                resp.succ();
                GameSessionHelper.sendPacket(session, resp);
                handlePlayerCreate(session, playerEntity);
            } else {
                playerService.unregisterNickName(nickName);
                playerService.clearAccount2PlayerId(asKey);
                resp.fail();
                GameSessionHelper.sendPacket(session, resp);
                ExceptionUtils.log("account :{}, server:{} 创角失败", account, serverId);
                return;
            }
        } catch (PlayerIdOverflowException e) {
            playerService.unregisterNickName(nickName);
            playerService.clearAccount2PlayerId(asKey);
            ExceptionUtils.log(e);
            resp.fail4OverFlow();
            GameSessionHelper.sendPacket(session, resp);
            return;
        } catch (ServerNotHoldedException e2) {
            playerService.unregisterNickName(nickName);
            playerService.clearAccount2PlayerId(asKey);
            ExceptionUtils.log(e2);
            resp.fail4NotServer();
            GameSessionHelper.sendPacket(session, resp);
            return;
        } catch (Exception e3) {
            playerService.unregisterNickName(nickName);
            playerService.clearAccount2PlayerId(asKey);
            ExceptionUtils.log(e3);
            resp.fail();
            GameSessionHelper.sendPacket(session, resp);
            return;
        }
    }

    public void handlePlayerCreate(final GameSession session, PlayerEntity playerEntity) {
        final long playerId = playerEntity.getPlayerId();
        PlayerActor playerActor = new PlayerActor();
        playerActor.setPlayerId(playerId);
        playerActor.setPlayerEntity(playerEntity);
        playerActor.setInit(true);
        ListenerHandler.getInstance().firePlayerCreateListener(playerActor, session);
        new CreatePlayerLogEvent(playerActor, session.getPid(), session.getGid(), session.getParam()).post();
        playerActor = playerService.cachePlayerActor(playerId, playerActor);
        session.putUnknowArgs("player", playerActor);
    }

    @Override
    public void loginAsk(GameSession session, ReqLoginAskPacket packet) {
        handlePlayerLogin(session);
    }

    @Override
    public void reconnect(GameSession session, ReqReconnectPacket packet) {
        logger.debug("handlePlayerReconnect {}", packet.getAccount());
        String asKey = packet.getAccount() + "_" + packet.getServerId();
        ConnectInfo connectInfo = asKey2ConnectInfoCache.getIfPresent(asKey);
        GameSession oldSession = onlineService.getSession(packet.getAccount());
        RespReconnectPacket resp = PacketFactory.createPacket(PacketId.Base.RESP_RECONNECT);

        if (connectInfo == null && oldSession == null) {
            resp.refresh();
            GameSessionHelper.sendPacket(session, resp);
            logger.debug("{} reconnect refresh 1", packet.getAccount());
            return;
        }

        String connectKey = connectInfo != null ? connectInfo.getConnectKey() : oldSession.getConnectKey();
        int respSN = connectInfo != null ? connectInfo.getRespSN() : oldSession.getRespSN();

        if (StringUtils.isBlank(connectKey) || !connectKey.equals(packet.getConnectKey())) { //校验不通过
            resp.refresh();
            GameSessionHelper.sendPacket(session, resp);
            logger.debug("{} reconnect refresh 2", packet.getAccount());
            return;
        }

        //校验通过后
        if (oldSession != null && oldSession.isClose() && !oldSession.getCloseCause().isRelogin()) { //主动关闭
            resp.refresh();
            GameSessionHelper.sendPacket(session, resp);
            logger.debug("{} reconnect refresh 3", packet.getAccount());
            return;
        }

        if (!session.compareAndSetStatus(GameSessionStatus.INIT, GameSessionStatus.AUTHED)) {
            resp.refresh();
            GameSessionHelper.sendPacket(session, resp);
            logger.debug("{} reconnect refresh 4", packet.getAccount());
            return;
        }

        LoginAuthParam loginAuthParam = connectInfo != null ? connectInfo.getLoginAuthParam() : oldSession.getLoginAuthParam();
        session.setLoginAuthParam(loginAuthParam);

        //判断是否存在
        if (oldSession == null) {
            resp.relogin();
            GameSessionHelper.sendPacket(session, resp);
            logger.debug("{} reconnect relogin 1", packet.getAccount());
            return;
        }

        if (respSN != packet.getRespSN()) {
            if (oldSession.isActive()) {//前端已经断开
                oldSession.close(CloseCause.CLIENT_NET_ERROR);
                logger.debug("{} 前端已经断开", packet.getAccount());
            }
            resp.relogin();
            GameSessionHelper.sendPacket(session, resp);
            logger.debug("{} reconnect relogin 2", packet.getAccount());
            return;
        }

        synchronized (oldSession) {
            if (!oldSession.isExiting()) {
                Channel oldChannel = oldSession.changeChannel(session.getChannel());
                ChannelUtils.clearAndCloseChannel(oldChannel); //清除引用，关闭链接（可能后段连接还没断）
                ChannelUtils.replaceChannelSession(oldSession.getChannel(), oldSession);
                resp.success();
                GameSessionHelper.sendPacket(session, resp);
                logger.debug("{} reconnect success", packet.getAccount());
            } else {
                resp.relogin();
                GameSessionHelper.sendPacket(session, resp);
                logger.debug("{} reconnect relogin 3", packet.getAccount());
            }
        }

    }

    @Override
    public void systemHeartbeat(GameSession session, ReqSystemHeartbeatPacket packet) {

    }

    @Override
    public void reqRandomNamePacket(GameSession session, ReqRandomNameShowPacket packet) {
        Collection<String> names = playerService.getAllPlayerName();
        String name;
        if (names.size() > 50) {
            List<String> nameList = new ArrayList<>(names);
            name = RandomUtils.randEqualPro(nameList);
        } else {
            name = getRandomName(RandomUtils.nextBoolean() ? Gender.MALE.getId() : Gender.FEMALE.getId());
        }
        if (name != null) {
            RespRandomnamePacket resp = PacketFactory.createPacket(PacketId.Base.RESP_NAME_RANDOM_SHOW);
            resp.init(name);
            GameSessionHelper.sendPacket(session, resp);
        }
    }

    public void invalidateAllConnectInfo() {
        asKey2ConnectInfoCache.invalidateAll();
    }

    public void invalidatePlayerConnectInfo(String account, int serverId) {
        String key = genKey(account, serverId);
        asKey2ConnectInfoCache.invalidate(key);
    }

    /**
     * 获取随机角色名
     * @param sex
     * @return
     */
    private String getRandomName(int sex) {
        String name = null;
        //随机50次，没有合适的返回null
        for (int i = 0; i < 50; i++) {
            name = configRandomNameManger.getName(sex == 1);
            if (name != null && !name.equals("") && !playerService.isNameExist(name) && wordService.isAvailableFormatForCreate(name)) {
                break;
            }
            name = null;
        }
        return name;
    }

    @Override
    public int getOrder() {
        return ServerStarter.LOWEST;
    }

    @Override
    public void init() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("version")) {
            if (Objects.isNull(in)) {
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            messageVersion = reader.readLine();
        } catch (Exception e) {
            ExceptionUtils.log(e);
        }
    }
}
