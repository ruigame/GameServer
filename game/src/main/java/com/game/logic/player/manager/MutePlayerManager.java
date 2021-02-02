package com.game.logic.player.manager;

import com.game.async.asyncdb.manager.CacheManager;
import com.game.logic.common.PlayerActor;
import com.game.logic.player.dao.MutePlayerDao;
import com.game.logic.player.entity.MutePlayer;
import com.game.logic.player.listener.PlayerLoginListener;
import com.game.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午11:22
 */
@Component
public class MutePlayerManager extends CacheManager<Long, MutePlayer> implements PlayerLoginListener {

    private static final Logger log = LoggerFactory.getLogger(MutePlayerManager.class);

    @Autowired
    private MutePlayerDao mutePlayerDao;

    @Override
    public MutePlayer getFromDB(Long key) {
        return mutePlayerDao.get(key);
    }

    @Override
    public MutePlayer create(Long key, Object...args) {
        return new MutePlayer(key);
    }

    @Override
    public void onLogin(PlayerActor playerActor) {
        long playerId = playerActor.getId();
        MutePlayer mutePlayer = getItem(playerId);
        if (mutePlayer == null) {
            mutePlayer = new MutePlayer();
            mutePlayer.setPlayerId(playerId);
            mutePlayer.setMuteStartTime(TimeUtils.timestamp());
            mutePlayer.insert();
            addCache(playerId, mutePlayer);
        }
    }

    /**
     * 禁言活着解禁
     */
    public void handle(long playerId, int muteTime, String reason) {
        MutePlayer mutePlayer = getItem(playerId);
        if (muteTime == 0) {
            if (mutePlayer == null) {
                return;
            }
            releaseMutePlayer(mutePlayer);
            return;
        }
        if (mutePlayer == null) {
            mutePlayer = new MutePlayer();
            mutePlayer.setPlayerId(playerId);
            mutePlayer.setMuteStartTime(TimeUtils.timestamp());
            mutePlayer.insert();
            addCache(playerId, mutePlayer);
        }

        mutePlayer.setMuteStartTime(TimeUtils.timestamp());
        mutePlayer.setMuteTime(muteTime);
        mutePlayer.setReason(reason);
        mutePlayer.update();
        log.info("player(id = {}) 禁言中.....", playerId);
    }

    public void releaseMutePlayer(MutePlayer mutePlayer) {
        if (mutePlayer.getMuteStartTime() > 0) {
            mutePlayer.release();
            mutePlayer.update();
            log.info("player(id={})解言中....", mutePlayer.getPlayerId());
        }
    }

    public boolean isPlayerMute(long playerId) {
        MutePlayer mutePlayer = getItem(playerId);
        if (mutePlayer != null) {
            if (mutePlayer.isMute()) {
                return true;
            }
            releaseMutePlayer(mutePlayer);
            return false;
        }
        return false;
    }
}
