package com.game.logic.player.entity;

import com.game.async.asyncdb.anotation.Persistent;
import com.game.async.asyncdb.orm.BaseDBEntity;
import com.game.logic.player.dao.ForbidIpEntityDao;
import com.game.logic.player.domain.forbid.ForbidIpDataKey;

import javax.persistence.*;

/**
 * 封号，某ip某服
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:02
 */
@Entity
@Table(name = "ForbidIpEntity")
@Persistent(syncClass = ForbidIpEntityDao.class)
@IdClass(ForbidIpDataKey.class)
public class ForBidIpEntity extends BaseDBEntity {

    @Column(columnDefinition = "varchar(30", nullable = false, updatable = false)
    @Id
    private String ip;

    @Id
    private int serverId;

    private int forbidTime;

    private int limitTime;

    @Column(columnDefinition = "varchar(500")
    private String reason;

    public ForBidIpEntity() {

    }

    public ForBidIpEntity(String ip, int serverId) {
        this.ip = ip;
        this.serverId = serverId;
    }

    public boolean isForbidTimeOut(int utime) {
        if (this.limitTime <= 0) {
            return true;
        }

        int minute = utime / 60;
        int forbidMinute = this.forbidTime / 60;
        if (minute - forbidMinute >= this.limitTime) {
            return true;
        }
        return false;
    }

    public int forbidLessTime(int utime) {
        if (this.limitTime <= 0) {
            return 0;
        }
        int minute = utime / 60;
        int forbidMinute = this.forbidTime / 60;
        return this.limitTime - (minute - forbidMinute);
    }

    public void updateForbidIp(int forbidTime, int limitTime, String reason) {
        this.forbidTime = forbidTime;
        this.limitTime = limitTime;
        this.reason = reason;
        update();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getForbidTime() {
        return forbidTime;
    }

    public void setForbidTime(int forbidTime) {
        this.forbidTime = forbidTime;
    }

    public int getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(int limitTime) {
        this.limitTime = limitTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
