package com.game.logic.player.entity;

import com.game.async.asyncdb.anotation.Persistent;
import com.game.async.asyncdb.orm.BaseDBEntity;
import com.game.logic.player.dao.ForbidRoleEntityDao;
import com.game.logic.player.domain.forbid.Forbid;
import com.game.logic.player.domain.forbid.ForbidEntityKey;

import javax.persistence.*;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:22
 */
@Entity
@Table(name ="ForbidRoleEntity")
@Persistent(syncClass = ForbidRoleEntityDao.class)
@IdClass(ForbidEntityKey.class)
public class ForbidRoleEntity extends BaseDBEntity implements Forbid {

    @Column(columnDefinition = "varchar(50)", nullable = false, updatable = false)
    @Id
    private String account; //账号

    @Column(columnDefinition = "varchar(50)", nullable = false, updatable = false)
    @Id
    private String server;

    private int forbidTime;

    private int limitTime;

    @Column(columnDefinition = "varchar(500)")
    private String reason;

    public ForbidRoleEntity(){}

    public ForbidRoleEntity(String account, String server) {
        this.account = account;
        this.server = server;
    }

    @Override
    public boolean isForbidTimeOut(int utime) {
        if (this.limitTime <= 0) {
            return true;
        }
        int uMinute = utime / 60;
        int forbidMinute = this.forbidTime / 60;

        if (uMinute - forbidMinute >= this.limitTime) {
            return true;
        }
        return false;
    }

    @Override
    public int forbidLessTime(int utime) {
        if (this.limitTime <= 0) {
            return 0;
        }
        int uMinute = utime / 60;
        int forbidMinute = this.forbidTime / 60;
        return this.limitTime - (uMinute - forbidMinute);
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
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
