package com.game.base;

import com.game.logic.player.BaseService;
import com.game.logic.player.PlayerService;
import com.game.util.Context;
import com.game.util.GameSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    /**
     * 根据玩家id获取在线玩家
     * @param playerId
     * @return
     */
    public PlayerActor getOnlinePlayer(long playerId) {
        GameSession session = PLAYERID_TO_SESSION.get(playerId);
        if (session == null) return null;
        PlayerActor player = Context.getBean(PlayerService.class).getPlayerActor(playerId);
        return player;
    }
}
