package com.game.logic.player.entity;

import com.game.async.asyncdb.anotation.Persistent;
import com.game.async.asyncdb.orm.BaseDBEntity;
import com.game.logic.player.dao.MutePlayerDao;
import com.game.util.TimeUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * 禁言
 * @Author: liguorui
 * @Date: 2021/1/21 下午11:15
 */
@Entity
@Table(name = "MutePlayer")
@Persistent(syncClass = MutePlayerDao.class)
public class MutePlayer extends BaseDBEntity {

    @Id
    private long playerId;
    /**
     * 开始禁言时间 秒
     */
    private int muteStartTime;

    /**
     * 禁言多久
     */
    private int muteTime;

    private String reason;

    public MutePlayer() {}

    public MutePlayer(long playerId) {
        this.playerId = playerId;
    }

    public boolean isMute() {
        if (muteTime == -1) {//永久禁言
            return true;
        }
        if (muteTime == 0) {
            return false;
        }

        int now = TimeUtils.timestamp();
        if (now - muteStartTime <= muteTime) {
            return true;
        }
        return false;
    }

    public void release() {
        muteTime = 0;
        muteStartTime = 0;
        reason = "";
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getMuteStartTime() {
        return muteStartTime;
    }

    public void setMuteStartTime(int muteStartTime) {
        this.muteStartTime = muteStartTime;
    }

    public int getMuteTime() {
        return muteTime;
    }

    public void setMuteTime(int muteTime) {
        this.muteTime = muteTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutePlayer that = (MutePlayer) o;
        return playerId == that.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerId);
    }
}
