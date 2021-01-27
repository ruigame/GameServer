package com.game.logic.admin;

import com.game.PacketId;
import com.game.base.GameSessionHelper;
import com.game.logic.admin.packet.req.ReqAdminKickByIdPacket;
import com.game.logic.admin.packet.req.ReqAdminTestPacket;
import com.game.logic.admin.packet.resp.RespResultPacket;
import com.game.logic.common.ConfigService;
import com.game.logic.common.PlayerActor;
import com.game.logic.player.BaseService;
import com.game.logic.player.PlayerService;
import com.game.net.CloseCause;
import com.game.net.packet.AbstractPacket;
import com.game.net.packet.PacketFactory;
import com.game.net.packet.PacketHandlerWrapper;
import com.game.thread.message.IMessage;
import com.game.util.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午2:40
 */
@Component
public class AdminService implements IAdminService, ServerStarter {

    private Logger log = LoggerFactory.getLogger("AdminLogger");

    public static final Logger rechareLog = LoggerFactory.getLogger("Recharge");

    private static final int cacheSize = 50000;

    private Cache<String, Boolean> rechargePayDataCache = CacheBuilder.newBuilder().maximumSize(cacheSize).build();

    private ExecutorService executor = new ThreadPoolExecutor(1, 60, 300L,
            TimeUnit.SECONDS, new SynchronousQueue<>(), new SimpleThreadFactory("admin-packet-executor"));

    public void execute(final PacketHandlerWrapper wrapper, final GameSession session, final AbstractPacket packet) {
//        log.info("rec {} {} [{}] {}", DateUtils.format(new Date(packet.getCreateTime())),
//                packet.getCmd(), ClassUtils.printlnAllFields(packet), session.getChannel());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    long st = System.currentTimeMillis();
                    wrapper.invoke(session, packet);
                    long usedTime = System.currentTimeMillis() - st;
                    log.info("{} {} {} {}", DateUtils.format(new Date(packet.getCreateTime())),
                            usedTime, packet.toString(), session.getChannel());
                } catch (Exception e) {
                    e.printStackTrace();
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        ExceptionUtils.log("packet:{}", packet.getCmd(), cause);
                    } else {
                        ExceptionUtils.log("packet:{}", packet.getCmd(), e);
                    }
                }
            }
        });
    }

    public void sendSuccess(GameSession session) {
        sendResult(session, 1);
    }

    public void sendFailed(GameSession session) {
        sendResult(session, 0);
    }

    public void sendResult(GameSession session, int result) {
        RespResultPacket packet = PacketFactory.createPacket(PacketId.Admin.RESP_RESULT_PACKET);
        packet.result(result);
        GameSessionHelper.sendPacket(session, packet);
    }

    @Override
    public void reqAdminTest(GameSession session, ReqAdminTestPacket req) {
        sendSuccess(session);
    }

    @Override
    public void reqAdminKickPlayerByAccount(GameSession session, ReqAdminKickByIdPacket req) {
        Set<String> accounts = req.getAccounts();
        int server = Context.getBean(ConfigService.class).getOriServerId();
        for (String account : accounts) {
            String key = account + "_" + server;
            PlayerActor playerActor = Context.getBean(PlayerService.class).getPlayerByASKey(key);
            if (playerActor == null) continue;
            playerActor.addMessage(new IMessage<PlayerActor>() {
                @Override
                public void execute(PlayerActor playerActor) {
                    Context.getBean(BaseService.class).invalidatePlayerConnectInfo(account, server);
                    playerActor.closeSession(CloseCause.KICK);
                }
            });
        }
    }

    @Override
    public void init() {

    }
}
