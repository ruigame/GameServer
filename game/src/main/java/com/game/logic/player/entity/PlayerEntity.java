package com.game.logic.player.entity;

import com.game.async.asyncdb.orm.BaseDBEntity;
import com.game.async.asyncdb.anotation.Persistent;
import com.game.async.asynchttp.HttpUtils;
import com.game.logic.player.dao.PlayerEntityDao;
import com.game.logic.player.domain.Role;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 玩家实体类
 * @Author: liguorui
 * @Date: 2020/9/20 5:56 下午
 */
@Entity
@Table(name="Player")
@Persistent(syncClass = PlayerEntityDao.class)
@NamedQueries({
        @NamedQuery(name = "getAllName", query = "SELECT name, playerId FROM PlayerEntity "),
        @NamedQuery(name = "getAllAccount", query = "SELECT account, serverId, playerId FROM PlayerEntity "),
        @NamedQuery(name = "getAllServerIds", query = "SELECT DISTINCT (serverId)FROM PlayerEntity "),
        @NamedQuery(name = "getMaxIdByServerId", query = "SELECT MAX(playerId) FROM PlayerEntity WHERE serverId =:serverId"),
        @NamedQuery(name = "selectPayNum", query = "SELECT count(1) FROM PlayerEntity where totalPay > 0"),
        @NamedQuery(name = "getDuplicateName", query = "SELECT p1 FROM PlayerEntity p1 where exists (select name from PlayerEntity  p2 " +
                " where p1.name=p2.name and p1.playerId<>p2.playerId) ORDER BY name"),
        @NamedQuery(name = "countTotalPay", query = "SELECT sum(totalPay) FROM PlayerEntity "),
        @NamedQuery(name = "getMaxLevel", query = "SELECT max(level) FROM PlayerEntity "),
})
public class PlayerEntity extends BaseDBEntity {

    @Id
    private long playerId; //id

    @Column(columnDefinition = "varchar(50)", nullable = false, updatable = false)
    private String account; //账号

    private int serverId;

    @Column(columnDefinition = "varchar(50)", nullable = false)
    private String name;

    @Column(columnDefinition = "text", nullable = false, updatable = true)
    private String params;

    @Transient
    private Map<String, String> paramMap;

    /**
     * 玩家等级
     */
    private int level;

    /**
     * 玩家经验
     */
    private long exp;

    /**
     * 元宝
     */
    private long gold;

    /**
     * 总充值
     */
    private int totalPay;

    /**
     * 工会id
     */
    private long guildId;

    /**
     * 当前场景id
     */
    private int sceneId;

    private int x;

    private int y;

    /**
     * 最近登陆时间
     */
    private long loginTime;

    /**
     * 创角ip
     */
    private String createIp;

    /**
     * 角色信息
     */
    @Column(columnDefinition = "text COMMENT '角色'", nullable = false)
    private String roles;

    @Transient
    public List<Role> roleList;

    /**
     * 显示的角色下标
     */
    private int showRoleIndex = 1;

    public PlayerEntity() {

    }

    public PlayerEntity(int serverId, long playerId, String account, String name, String params,
                        int sceneId, int x, int y, int level, String ip) {
        this.serverId = serverId;
        this.playerId = playerId;
        this.account = account;
        this.name = name;
        this.params = params;
        this.sceneId = sceneId;
        this.x = x;
        this.y = y;
        this.level = level;
        this.createIp = ip;
        initParams();
    }

    public void initParams() {
        if (StringUtils.isNotBlank(params)) {
            this.paramMap = HttpUtils.parseQueryString(params);
        } else {
            this.paramMap = Collections.emptyMap();
        }
    }

    public void addRole(Role role) {
        roleList.add(role);
    }

    public void showRole(int roleIndex) {
        this.showRoleIndex = roleIndex;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getGold() {
        return gold;
    }

    public void setGold(long gold) {
        this.gold = gold;
    }

    public int getTotalPay() {
        return totalPay;
    }

    public void setTotalPay(int totalPay) {
        this.totalPay = totalPay;
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public String getCreateIp() {
        return createIp;
    }

    public void setCreateIp(String createIp) {
        this.createIp = createIp;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
