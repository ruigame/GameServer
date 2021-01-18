package com.game.base;

import com.game.log.LogAppender;
import com.game.log.LogEvent;
import com.game.player.entity.PlayerEntity;

/**
 * @Author: liguorui
 * @Date: 2020/12/2 下午9:43
 */
public abstract class PlayerLogEvent extends LogEvent {

    protected String platform; //平台
    protected int sid; //区服id
    protected String pid;
    protected String gid;
    protected String account;
    protected long playerId;
    protected String playerName;
    protected long time;
    protected String param;
    protected int openServerDay; //开服天数

    public PlayerLogEvent() {

    }

    public PlayerLogEvent(String platform, int sid, String account, long playerId, String playerName, String pid, String gid, String param) {
        super();
        this.platform = platform;
        this.sid = sid;
        this.account = account;
        this.playerId = playerId;
        this.time = System.currentTimeMillis();
        this.pid = pid;
        this.gid = gid;
        this.param = param;
        this.openServerDay = OnlineUtils.getDayAfterOpenServer();
    }

//    public PlayerLogEvent(PlayerActor playerActor) {
//
//    }

    public PlayerLogEvent(PlayerEntity playerEntity) {

    }

    public long getPlayerId() {
        return playerId;
    }

    public long getTime() {
        return time;
    }

    public LogAppender messagePlayerActor() {
        return LogAppender.create(256)
                .appendKeyValue("time", time)
                .appendKeyValue("platform", platform)
                .appendKeyValue("sid", sid)
                .appendKeyValue("pid", pid)
                .appendKeyValue("gid", gid)
                .appendKeyValue("account", account)
                .appendKeyValue("playerId", playerId)
                .appendKeyValue("playerName", playerName)
                .appendKeyValue("openServerDay", openServerDay)
                ;
    }
}
