package com.game.base;

import com.game.net.packet.AbstractPacket;
import com.game.logic.player.entity.PlayerEntity;
import com.game.thread.message.MessageHandler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午4:59
 */
public class PlayerActor extends MessageHandler<PlayerActor> implements IPlayer<PlayerActor> {

    private PlayerEntity playerEntity;

    private long playerId;

    /**
     * 是否登陆
     */
    private AtomicBoolean isLogin = new AtomicBoolean(false);

    /**
     * 是否丢弃请求包
     */
    private volatile  boolean discardReqPacet;

    /**
     * 初始化加载模块数据
     */
    private volatile boolean init;

    public PlayerActor(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public PlayerActor() {
    }

    public String getAccount() {
        return playerEntity.getAccount();
    }

    public boolean isLogined() {
        return isLogin.get();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public long getPlayerId() {
        return 0;
    }

    @Override
    public void sendPacket(AbstractPacket packet) {

    }

    @Override
    public void tryEnterScene() {

    }

    public boolean isDiscardReqPacet() {
        return discardReqPacet;
    }

    public void setDiscardReqPacet(boolean discardReqPacet) {
        this.discardReqPacet = discardReqPacet;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public int getLevel() {
        return playerEntity.getLevel();
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getId() {
        return playerId;
    }

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    public void setPlayerEntity(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }
}
