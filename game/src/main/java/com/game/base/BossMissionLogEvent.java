package com.game.base;

/**
 * boss副本日志
 * @Author: liguorui
 * @Date: 2020/12/2 下午9:55
 */
public class BossMissionLogEvent extends PlayerLogEvent{

    private int type;

    private int missionId;

    public BossMissionLogEvent() {
        super();
    }

//    public BossMissionLogEvent(PlayerActor playerActor, int type, int missionId) {
//        super(playerActor);
//        this.type = type;
//        this.missionId = missionId;
//    }

    @Override
    public String message() {
        return messagePlayerActor()
                .appendKeyValue("type", type)
                .appendKeyValue("missionId", missionId)
                .toString();
    }
}
