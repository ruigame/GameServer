package com.game.logic.player.domain;

import com.game.net.packet.Response;

import java.util.List;
import java.util.Map;

/**
 * 玩家信息
 * @Author: liguorui
 * @Date: 2021/1/18 下午10:24
 */
public class PlayerInfoVo {

    private long playerId;
    private String name;
    private int sceneId;
    private int level;
    private int vipLevel;
    private long guildId;
    private String guildName;
    private List<RoleInfoVo> roleInfos;

    private long exp;
    private long maxExp;

    private Map<PlayerResourceType, Long> resMap;

    private int showRoleIndex;
    private String connectKey;

    public void write(Response response) {
        response.writeLong(playerId);
        response.writeString(name);
        response.writeInt(level);
        response.writeByte(roleInfos.size());
        for (RoleInfoVo roleInfoVo : roleInfos) {
            roleInfoVo.write(response);
        }
        response.writeInt(resMap.size());
        for (Map.Entry<PlayerResourceType, Long> entry : resMap.entrySet()) {
            response.writeByte(entry.getKey().getCode());
            response.writeLong(entry.getValue());
        }
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public List<RoleInfoVo> getRoleInfos() {
        return roleInfos;
    }

    public void setRoleInfos(List<RoleInfoVo> roleInfos) {
        this.roleInfos = roleInfos;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(long maxExp) {
        this.maxExp = maxExp;
    }

    public Map<PlayerResourceType, Long> getResMap() {
        return resMap;
    }

    public void setResMap(Map<PlayerResourceType, Long> resMap) {
        this.resMap = resMap;
    }

    public int getShowRoleIndex() {
        return showRoleIndex;
    }

    public void setShowRoleIndex(int showRoleIndex) {
        this.showRoleIndex = showRoleIndex;
    }

    public String getConnectKey() {
        return connectKey;
    }

    public void setConnectKey(String connectKey) {
        this.connectKey = connectKey;
    }
}
