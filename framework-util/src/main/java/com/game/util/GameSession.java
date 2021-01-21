package com.game.util;

import com.game.async.asynchttp.HttpUtils;
import com.game.net.ChannelUtils;
import com.game.net.CloseCause;
import com.game.net.LoginAuthParam;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 8:17 下午
 */
public class GameSession {

    private static final Logger log = LoggerFactory.getLogger(GameSession.class);

    private static final AtomicReferenceFieldUpdater<GameSession, GameSessionStatus> StateUpdater
            = AtomicReferenceFieldUpdater.newUpdater(GameSession.class, GameSessionStatus.class, "status");

    private volatile GameSessionStatus status = GameSessionStatus.INIT;

    private volatile Channel channel;

    /**
     * 登陆参数
     */
    private LoginAuthParam loginAuthParam;

    private String account_server;

    private volatile long playerId;

    private volatile String ip;

    private volatile long ipHashCode;

    /**
     * 是否正在退出标记
     */
    private AtomicBoolean exiting = new AtomicBoolean();

    /**
     * 是否关闭session
     */
    private volatile boolean close;

    private volatile CloseCause closeCause;

    private Map<String, Object> unkonwArgs;

    private AtomicBoolean reflushChannelSchedule = new AtomicBoolean();

    private String connectKey;

    private AtomicInteger respSN = new AtomicInteger(); //相应序列号

    private Map<String, String> paramMap = new HashMap<>();

    public GameSession(Channel channel) {
        this.channel = channel;
        this.ip = ChannelUtils.getIp(channel);
        this.ipHashCode = IpAddressUtils.hashCode(ip);
    }

    public Channel changeChannel(Channel channel) {
        Channel oldChannel = this.channel;
        this.channel = channel;
        this.ip = ChannelUtils.getIp(channel);
        this.ipHashCode = IpAddressUtils.hashCode(ip);
        return oldChannel;
    }

    public boolean compareAndSetStatus(GameSessionStatus expect, GameSessionStatus update) {
        return StateUpdater.compareAndSet(this, expect, update);
    }

    public String getAccount() {
        return loginAuthParam == null ? "" : loginAuthParam.getAccount();
    }

    public ChannelFuture close(CloseCause cause, String... ip) {
        this.close = true;
        this.closeCause = cause;
        if (channel.isOpen()) {
            log.info("CloseSession[{}] due to Cause[{}] {}", getAccount(), cause, ip);
            return channel.close();
        } else {
            log.info("AlreadyCloseSession[{}] due to Cause[{}] {}", getAccount(), cause, ip);
            return null;
        }
    }

    public boolean isRegister() {
        return status.ordinal() >= GameSessionStatus.REGISTER.ordinal();
    }

    public boolean isAuthed() {
        return status.ordinal() >= GameSessionStatus.AUTHED.ordinal();
    }

    public boolean setExiting() {
        return this.exiting.compareAndSet(false, true);
    }

    public boolean isExiting() {
        return this.exiting.get();
    }

    /**
     * 是否连接正常
     * @return
     */
    public boolean isActive() {
        return channel != null && channel.isActive();
    }

    public GameSessionStatus getStatus() {
        return status;
    }

    public void setStatus(GameSessionStatus status) {
        this.status = status;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public LoginAuthParam getLoginAuthParam() {
        return loginAuthParam;
    }

    public void setLoginAuthParam(LoginAuthParam loginAuthParam) {
        this.loginAuthParam = loginAuthParam;
        setParamMap(HttpUtils.parseQueryString(loginAuthParam.getParam()));
        genASKey();
    }

    private void genASKey() {
        this.account_server = getASKey(loginAuthParam.getAccount(), loginAuthParam.getServerId());
    }

    public String getASKey(String account, int server) {
        return account + "_" + server;
    }

    public String getASKey() {
        return account_server;
    }

    public int getServerId() {
        return loginAuthParam == null ? 0 : loginAuthParam.getServerId();
    }

    public void ensureUnknowArgs() {
        if (this.unkonwArgs == null) {
            this.unkonwArgs = Maps.newHashMap();
        }
    }

    /**
     * 生成重连key
     * @return
     */
    public String produceReconnectKey() {
        this.connectKey = UUID.randomUUID().toString();
        return this.connectKey;
    }

    public String getAccount_server() {
        return account_server;
    }

    public void setAccount_server(String account_server) {
        this.account_server = account_server;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getIpHashCode() {
        return ipHashCode;
    }

    public void setIpHashCode(long ipHashCode) {
        this.ipHashCode = ipHashCode;
    }

    public AtomicBoolean getExiting() {
        return exiting;
    }

    public void setExiting(AtomicBoolean exiting) {
        this.exiting = exiting;
    }

    public boolean isClose() {
        return close;
    }

    public boolean isBackend() {
        return "1".equals(paramMap.get("backend"));
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public CloseCause getCloseCause() {
        return closeCause;
    }

    public void setCloseCause(CloseCause closeCause) {
        this.closeCause = closeCause;
    }

    public Map<String, Object> getUnkonwArgs() {
        this.ensureUnknowArgs();
        return unkonwArgs;
    }

    public void setUnkonwArgs(Map<String, Object> unkonwArgs) {
        this.unkonwArgs = unkonwArgs;
    }

    public AtomicBoolean getReflushChannelSchedule() {
        return reflushChannelSchedule;
    }

    public void setReflushChannelSchedule(AtomicBoolean reflushChannelSchedule) {
        this.reflushChannelSchedule = reflushChannelSchedule;
    }

    public String getConnectKey() {
        return connectKey;
    }

    public String getParam() {
        return loginAuthParam == null ? "" : loginAuthParam.getParam();
    }

    public void setConnectKey(String connectKey) {
        this.connectKey = connectKey;
    }

    public int getRespSN() {
        return respSN.get();
    }

    public int inRespSN() {
        return respSN.incrementAndGet();
    }

    public void setRespSN(AtomicInteger respSN) {
        this.respSN = respSN;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }
}
