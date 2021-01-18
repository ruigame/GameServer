package com.game.logic.guild.entity;

import com.game.async.asyncdb.anotation.Persistent;
import com.game.async.asyncdb.orm.BaseDBEntity;
import com.game.logic.guild.dao.GuildMemberDAO;

import javax.persistence.*;

/**
 * 公会成员
 * @Author: liguorui
 * @Date: 2021/1/14 下午10:51
 */
@Entity
@Table(name="GuildMember")
@Persistent(syncClass = GuildMemberDAO.class)
@NamedQueries({
        @NamedQuery(name="getByGuildId", query = "FROM GuildMember WHERE guildId =:guildId"),
        @NamedQuery(name="getAllNoGuildMembers", query = "FROM GuildMember WHERE guildId = 0"),
})
public class GuildMember extends BaseDBEntity {

    /**
     * 玩家id
     */
    @Id
    private long playerId;
    /**
     * 公会id
     */
    private long guildId;
    /**
     * 加入时间戳
     */
    private int joinTime;
    /**
     * 公会职位 1：会长 2：长老 3：普通会员
     */
    private int jobType;
    /**
     * 公会商店数据
     */
    @Column(columnDefinition = "text")
    private String guildShop;

    @Override
    public void deserialize() {

    }

    @Override
    public void serialize() {

    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public int getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(int joinTime) {
        this.joinTime = joinTime;
    }

    public int getJobType() {
        return jobType;
    }

    public void setJobType(int jobType) {
        this.jobType = jobType;
    }

    public String getGuildShop() {
        return guildShop;
    }

    public void setGuildShop(String guildShop) {
        this.guildShop = guildShop;
    }
}
