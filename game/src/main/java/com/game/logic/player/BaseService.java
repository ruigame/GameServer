package com.game.logic.player;

import com.game.logic.common.OnlineService;
import com.game.logic.player.domain.ConnectInfo;
import com.game.logic.player.packet.req.*;
import com.game.util.GameSession;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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

    private Cache<String, ConnectInfo> asKey2ConnectInfoCache = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.MINUTES).build();

    @Override
    public void loginAuth(GameSession session, ReqLoginAuthPacket packet) {

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
