package com.game.logic.common;

import com.game.log.LogAppender;
import com.game.log.LogEvent;
import com.game.logic.player.entity.PlayerEntity;
import com.game.util.Context;

/**
 * @Author: liguorui
 * @Date: 2020/12/2 下午9:43
 */
public abstract class PlayerLogEvent extends LogEvent {

    protected String platform; //平台
    protected int serverId; //区服id
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

    public PlayerLogEvent(String platform, int serverId, String account, long playerId, String playerName, String pid, String gid, String param) {
        super();
        this.platform = platform;
        this.serverId = serverId;
        this.account = account;
        this.playerId = playerId;
        this.time = System.currentTimeMillis();
        this.pid = pid;
        this.gid = gid;
        this.param = param;
        this.openServerDay = OnlineUtils.getDayAfterOpenServer();
    }

    public PlayerLogEvent(PlayerActor playerActor) {
        this(Context.getBean(ConfigService.class).getPlatform(), Context.getBean(ConfigService.class).getOriServerId(),
                playerActor.getAccount(), playerActor.getId(), playerActor.getName(), playerActor.getPid(), playerActor.getGid(), playerActor.getParam());
    }

    public PlayerLogEvent(PlayerEntity playerEntity) {
        this(Context.getBean(ConfigService.class).getPlatform(), Context.getBean(ConfigService.class).getOriServerId(),
                playerEntity.getAccount(), playerEntity.getPlayerId(), playerEntity.getName(), playerEntity.getPid(), playerEntity.getGid(), playerEntity.getParams());
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
                .appendKeyValue("serverId", serverId)
                .appendKeyValue("pid", pid)
                .appendKeyValue("gid", gid)
                .appendKeyValue("account", account)
                .appendKeyValue("playerId", playerId)
                .appendKeyValue("playerName", playerName)
                .appendKeyValue("openServerDay", openServerDay)
                ;
    }
}
