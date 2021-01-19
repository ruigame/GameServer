package com.game.logic.player.entity;

import com.game.async.asyncdb.anotation.Persistent;
import com.game.async.asyncdb.orm.BaseDBEntity;
import com.game.async.asynchttp.HttpUtils;
import com.game.async.asynchttp.example.Player;
import com.game.base.OnlineService;
import com.game.base.PlayerActor;
import com.game.logic.player.dao.PlayerEntityDao;
import com.game.logic.player.domain.PlayerSkill;
import com.game.logic.player.domain.ResourceType;
import com.game.logic.player.domain.Role;
import com.game.logic.player.domain.RoleType;
import com.game.util.Context;
import com.game.util.TimeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
     * 代币元宝
     */
    private long insteadGold;

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
     * 下线时间
     */
    private long logoutTime;

    /**
     * 角色信息
     */
    @Column(columnDefinition = "text COMMENT '角色'", nullable = false)
    private String roles;

    @Transient
    public List<Role> roleList;

    /**
     * 是否在线
     */
    private boolean online;

    /**
     * 上次零点处理时间
     */
    private long lastZeroTime;

    /**
     * 上次充值时间
     */
    private long lastRechargeTime;

    /**
     * 是否充值过标示（0：否 1：是）
     */
    private int firstRecharge;

    /**
     * 创建角色时间
     */
    @Column(nullable = false, updatable = false)
    private long createTime;

    /**
     * 创建时的IP
     */
    @Column(columnDefinition = "char(16)", nullable = false, updatable = false)
    private String createIp;

    /**
     * 上次登陆IP
     */
    @Column(columnDefinition = "char(16)", nullable = false)
    private String lastIp;

    /**
     * 登陆天数
     */
    private int loginTimes;

    @Column(columnDefinition = "text COMMENT '技能'", nullable = false)
    private String skill;

    private String pid;
    private String gid;

    @Transient
    private PlayerSkill playerSkill;

    /**
     * 显示的角色下标
     */
    private int showRoleIndex = 1;

    /**
     * 玩家战斗属性
     */
    @Transient
    private Map<Integer, Map<ResourceType, Long>> role2FightAttrMap = Collections.emptyMap();

    @Column(columnDefinition = "mediumtext COMMENT '玩家战斗属性'", nullable = false)
    private String role2FightAttrMapStr;

    /**
     * 同步db开关，延迟同步
     */
    @Transient
    private AtomicBoolean syndbGate = new AtomicBoolean();

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

    public boolean alterLevel(int alter) {
        this.level += alter;
        immediateUpdate();
        return true;
    }

    public void changeExp(long exp) {
        this.exp = exp;
    }

    public void alterExp(long alter) {
        exp += alter;
        if (exp < 0) {
            exp = 0;
        }
        update();
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

    public long getInsteadGold() {
        return insteadGold;
    }

    public void setInsteadGold(long insteadGold) {
        this.insteadGold = insteadGold;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getLastZeroTime() {
        return lastZeroTime;
    }

    public void updateLastZeroTime() {
        this.lastZeroTime = TimeUtils.currentTimeMillis();
        update();
    }

    public void login(String ip) {
        setLastIp(ip);
        updateLoginTime();
        update();
    }

    public void updateLoginTime() {
        long curTime = TimeUtils.currentTimeMillis();
        online = true;
        if (loginTime == 0) {
            loginTime = curTime;
            loginTimes = 1;
            return;
        }

        if (!TimeUtils.isSameDay(loginTime)) {
            loginTimes ++;
        }
        loginTime = curTime;
    }

    public void logout() {
        this.logoutTime = TimeUtils.currentTimeMillis();
        this.online = false;
        immediateUpdate();
    }

    public void setLastZeroTime(long lastZeroTime) {
        this.lastZeroTime = lastZeroTime;
    }

    public long getLastRechargeTime() {
        return lastRechargeTime;
    }

    public void setLastRechargeTime(long lastRechargeTime) {
        this.lastRechargeTime = lastRechargeTime;
    }

    public int getFirstRecharge() {
        return firstRecharge;
    }

    public void setFirstRecharge(int firstRecharge) {
        this.firstRecharge = firstRecharge;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public int getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(int loginTimes) {
        this.loginTimes = loginTimes;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public PlayerSkill getPlayerSkill() {
        return playerSkill;
    }

    public void setPlayerSkill(PlayerSkill playerSkill) {
        this.playerSkill = playerSkill;
    }

    public int getShowRoleIndex() {
        return showRoleIndex;
    }

    public void setShowRoleIndex(int showRoleIndex) {
        this.showRoleIndex = showRoleIndex;
    }

    public Map<Integer, Map<ResourceType, Long>> getRole2FightAttrMap() {
        return role2FightAttrMap;
    }

    public void setRole2FightAttrMap(Map<Integer, Map<ResourceType, Long>> role2FightAttrMap) {
        this.role2FightAttrMap = role2FightAttrMap;
    }

    public String getRole2FightAttrMapStr() {
        return role2FightAttrMapStr;
    }

    public void setRole2FightAttrMapStr(String role2FightAttrMapStr) {
        this.role2FightAttrMapStr = role2FightAttrMapStr;
    }

    public Role getRoleByIndex(int index) {
        return roleList.get(index - 1);
    }

    public long getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(long logoutTime) {
        this.logoutTime = logoutTime;
    }

    public boolean isRoleTypeExist(RoleType roleType) {
        for (Role role : roleList) {
            if (role.getRoleType() == roleType) {
                return true;
            }
        }
        return false;
    }

    public void changeName(String name) {
        this.name = name;
    }

    /**
     * 延迟同步，因为数据太大，更新太频繁，影响binlog日志，另外很多数据丢失也影响不大
     */
    @Override
    public void update() {
        if (syndbGate.compareAndSet(false, true)) {
            PlayerActor player = Context.getBean(OnlineService.class).getOnlinePlayer(playerId);
            if (player != null) {
                player.schedule("延迟持久化", (h)->{
                    syndbGate.set(false);
                    super.update();
                }, 10 , TimeUnit.MINUTES);
            } else {
                syndbGate.set(false);
                super.update();
            }
        }
    }

    public void immediateUpdate() {
        super.update();
    }
}
