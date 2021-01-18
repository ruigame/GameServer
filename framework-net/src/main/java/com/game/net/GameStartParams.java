package com.game.net;

import com.game.net.packet.Response;

import java.util.Set;

/**
 * @Author: liguorui
 * @Date: 2020/11/30 下午11:49
 */
public class GameStartParams {

    private static final GameStartParams gameStartParams = new GameStartParams();

    private NetServerBuild netServerBuild;

    private int pingPacketId;

    private Response pongResponse;

    private short[] responseEncoderExcludeIds;
    private short[] responseEncoderIncludeIds;

    private short[] crossPacketExcludes;

    /**
     * GameServerHandler
     */
    private short[] gameServerPacketIds;
    private boolean serverOpen;

    /**
     * GameServerIdleHandler
     */
    private Response gameServerIdleResponse;

    /**
     * AdminPacketHandler白名单ip
     */
    private String[] adminPatterns;
    private String adminKey;

    /**
     * CrossClientCheckerHandler
     */
    private String platform;
    private int serverId;
    private Set<Integer> serverIds;

    /**
     * CrossIdleHandler
     */
    private Response crossIdleResponse;
    private int crossPongId;

    /**
     * CrossResponseEncoder
     */
    private short[] crossResponseEncoderExcludeIds;

    public static GameStartParams getInstance() {
        return gameStartParams;
    }

    public NetServerBuild getNetServerBuild() {
        return netServerBuild;
    }

    public void setNetServerBuild(NetServerBuild netServerBuild) {
        this.netServerBuild = netServerBuild;
    }

    public int getPingPacketId() {
        return pingPacketId;
    }

    public void setPingPacketId(int pingPacketId) {
        this.pingPacketId = pingPacketId;
    }

    public Response getPongResponse() {
        return pongResponse;
    }

    public void setPongResponse(Response pongResponse) {
        this.pongResponse = pongResponse;
    }

    public short[] getResponseEncoderExcludeIds() {
        return responseEncoderExcludeIds;
    }

    public void setResponseEncoderExcludeIds(short[] responseEncoderExcludeIds) {
        this.responseEncoderExcludeIds = responseEncoderExcludeIds;
    }

    public short[] getResponseEncoderIncludeIds() {
        return responseEncoderIncludeIds;
    }

    public void setResponseEncoderIncludeIds(short[] responseEncoderIncludeIds) {
        this.responseEncoderIncludeIds = responseEncoderIncludeIds;
    }

    public short[] getCrossPacketExcludes() {
        return crossPacketExcludes;
    }

    public void setCrossPacketExcludes(short[] crossPacketExcludes) {
        this.crossPacketExcludes = crossPacketExcludes;
    }

    public short[] getGameServerPacketIds() {
        return gameServerPacketIds;
    }

    public void setGameServerPacketIds(short[] gameServerPacketIds) {
        this.gameServerPacketIds = gameServerPacketIds;
    }

    public boolean isServerOpen() {
        return serverOpen;
    }

    public void setServerOpen(boolean serverOpen) {
        this.serverOpen = serverOpen;
    }

    public Response getGameServerIdleResponse() {
        return gameServerIdleResponse;
    }

    public void setGameServerIdleResponse(Response gameServerIdleResponse) {
        this.gameServerIdleResponse = gameServerIdleResponse;
    }

    public String[] getAdminPatterns() {
        return adminPatterns;
    }

    public void setAdminPatterns(String[] adminPatterns) {
        this.adminPatterns = adminPatterns;
    }

    public String getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public Set<Integer> getServerIds() {
        return serverIds;
    }

    public void setServerIds(Set<Integer> serverIds) {
        this.serverIds = serverIds;
    }

    public Response getCrossIdleResponse() {
        return crossIdleResponse;
    }

    public void setCrossIdleResponse(Response crossIdleResponse) {
        this.crossIdleResponse = crossIdleResponse;
    }

    public int getCrossPongId() {
        return crossPongId;
    }

    public void setCrossPongId(int crossPongId) {
        this.crossPongId = crossPongId;
    }

    public short[] getCrossResponseEncoderExcludeIds() {
        return crossResponseEncoderExcludeIds;
    }

    public void setCrossResponseEncoderExcludeIds(short[] crossResponseEncoderExcludeIds) {
        this.crossResponseEncoderExcludeIds = crossResponseEncoderExcludeIds;
    }
}
