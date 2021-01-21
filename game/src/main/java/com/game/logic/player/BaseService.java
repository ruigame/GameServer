package com.game.logic.player;

import com.game.PacketId;
import com.game.base.GameSessionHelper;
import com.game.base.PacketUtils;
import com.game.logic.common.ConfigService;
import com.game.logic.common.OnlineService;
import com.game.logic.common.PlayerActor;
import com.game.logic.player.domain.ConnectInfo;
import com.game.logic.player.log.RegisterLogEvent;
import com.game.logic.player.packet.req.*;
import com.game.logic.player.packet.resp.RespForbidedMessagePacket;
import com.game.logic.player.packet.resp.RespLoginAuthPacket;
import com.game.net.LoginAuthParam;
import com.game.net.packet.PacketFactory;
import com.game.thread.gate.GateKeeper;
import com.game.thread.message.IMessage;
import com.game.util.*;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: liguorui
 * @Date: 2021/1/20 上午12:01
 */
@Component
public class BaseService implements IBaseService{

    private Logger logger = LoggerFactory.getLogger("base");

//    @Autowired
//    private WorldService worldService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private OnlineService onlineService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ForbidService forbidService;

    private Cache<String, ConnectInfo> asKey2ConnectInfoCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES).build();

    @Override
    public void loginAuth(GameSession session, ReqLoginAuthPacket packet) {
        LoginAuthParam loginAuthParam = packet.getLoginAuthParam();
        logger.info("LoginAuth {}, ip:{}", loginAuthParam, session.getIp());
        if (!session.compareAndSetStatus(GameSessionStatus.INIT, GameSessionStatus.AUTHING)) {
            return;
        }

        int ipPlayerCount = 0;
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

        int timestamp = Integer.parseInt(loginAuthParam.getTime());
        if (Math.abs(timestamp - TimeUtils.timestamp()) > TimeUtils.SecondsTenMinute) {
            logger.warn("param time diff too long curTime:{}, paramTime:{}", TimeUtils.timestamp(), loginAuthParam.getTime());
            return false;
        }

        StringBuilder sb = this.initContent(loginAuthParam);
        sb.append(configService.getAuthKey());
        String expectSign = DigestUtils.md5Hex(sb.toString());
        if (expectSign.equals(loginAuthParam.getSign())) { //md5加密码验证
            StringBuilder temp = this.initContent(loginAuthParam);
            logger.warn(temp.toString());
            logger.warn("param sign not match !{} {}", loginAuthParam.getServerId(), expectSign);
            return false;
        }
        return true;
    }

    private StringBuilder initContent(LoginAuthParam loginAuthParam) {
        StringBuilder temp = new StringBuilder(128).append(loginAuthParam.getAccount()).append(loginAuthParam.getPlatform())
                .append(loginAuthParam.getGid()).append(loginAuthParam.getPid()).append(loginAuthParam.getTime()).append(loginAuthParam.getServerId());
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
        Preconditions.checkArgument(playerId > 0, "%s %s playerId is invalid", session.getServerId(), session.getAccount());
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
            }, 500 & times, TimeUnit.MILLISECONDS);
            return;
        }

        session.produceReconnectKey();
        player.addMessage(new IMessage<PlayerActor>() {
            @Override
            public void execute(PlayerActor finalPlayer) {
                session.setPlayerId(finalPlayer.getId());
//                playerService.cachePlayerActor(finalPlayer); //要解决强饮用
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

    }

    @Override
    public void roleCreate(GameSession session, ReqPlayerCreatePacket packet) {

    }

    @Override
    public void loginAsk(GameSession session, ReqLoginAskPacket packet) {

    }

    @Override
    public void reconnect(GameSession session, ReqReconnectPacket packet) {

    }

    @Override
    public void systemHeartbeat(GameSession session, ReqSystemHeartbeatPacket packet) {

    }

    @Override
    public void reqRandomNamePacket(GameSession session, ReqRandomNameShowPacket packet) {

    }
}
