package com.game.logic.guild.entity;

import com.game.async.asyncdb.anotation.Persistent;
import com.game.async.asyncdb.orm.BaseDBEntity;
import com.game.logic.guild.dao.GuildDAO;
import com.google.common.collect.Sets;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: liguorui
 * @Date: 2021/1/14 下午10:20
 */
@Entity
@Table(name = "Guild")
@Persistent(syncClass = GuildDAO.class)
@NamedQueries({
        @NamedQuery(name = "getMaxGuildId", query = "SELECT MAX(id) FROM Guild "),
        @NamedQuery(name = "getAllValidGuilds", query = "FROM Guild WHERE status > 0"),
        @NamedQuery(name = "countByGuildName", query = "SELECT COUNT(1) FROM Guild WHERE name = :name"),
        @NamedQuery(name = "getDuplicateGuildName", query = "FROM Guild T1 WHERE EXISTS (SELECT name FROM Guild T2 WHERE T1.name = T2.name and T1.id <> T2.id) ORDER BY name")

})
public class Guild extends BaseDBEntity {

    /**
     * 公会唯一id
     */
    @Id
    private long id;
    /**
     * 公会名字
     */
    private String name;
    /**
     * 公会等级
     */
    private int level;
    /**
     * 公会状态 1:开放 2：满人 3：解散
     */
    private int status;
    /**
     * 创建时间
     */
    private int createTime;
    /**
     * 会长玩家id
     */
    private long chairmanPlayerId;
    /**
     * 公会公告
     */
    @Column(columnDefinition = "text")
    private String notice;

    /**
     * 公会会员Ids
     */
    @Transient
    private Set<Long> memberIdSet = new HashSet<>();

    @Override
    public void deserialize() {

    }

    @Override
    public void serialize() {

    }

    public Set<Long> getAllMemberIds() {
        synchronized (this) {
            return Sets.newHashSet(memberIdSet);
        }
    }

    public void addMember(long memberId) {
        synchronized (this) {
            this.memberIdSet.add(memberId);
        }
    }

    public void removeMember(long memberId) {
        synchronized (this) {
            memberIdSet.remove(memberId);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public long getChairmanPlayerId() {
        return chairmanPlayerId;
    }

    public void setChairmanPlayerId(long chairmanPlayerId) {
        this.chairmanPlayerId = chairmanPlayerId;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public Set<Long> getMemberIdSet() {
        return memberIdSet;
    }

    public void setMemberIdSet(Set<Long> memberIdSet) {
        this.memberIdSet = memberIdSet;
    }
}

