package com.game.logic.guild.manager;

import com.game.async.asyncdb.Synchronizer;
import com.game.base.PlayerActor;
import com.game.logic.guild.dao.GuildMemberDAO;
import com.game.logic.guild.entity.GuildMember;
import com.game.util.TimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: liguorui
 * @Date: 2021/1/14 下午11:06
 */
@Component
public class GuildMemberManager implements Synchronizer<GuildMember> {

    @Autowired
    private GuildMemberDAO memberDAO;

    /**
     * 公会成员映射 playerId -> GuildMember
     */
    private ConcurrentMap<Long, GuildMember> playerId2GuildMember = new ConcurrentHashMap<>();

    /**
     * 仅在服务器启动时候执行，检查没有公会成员的公会执行清除数据
     * @return
     */
    protected List<GuildMember> getAllNoGuildMembers() {
        List<GuildMember> noGuildMembersList = memberDAO.getAllNoGuildMembers();
        if (CollectionUtils.isNotEmpty(noGuildMembersList)) {
            for (GuildMember member : noGuildMembersList) {
                member.deserialize();
                ConcurrentUtils.putIfAbsent(playerId2GuildMember, member.getPlayerId(), member);
            }
        }
        return noGuildMembersList;
    }

    /**
     * 仅在服务器启动时候执行，加载有效公会的所有成员
     * @param guildId
     * @return
     */
    protected List<GuildMember> getAllMembersByGuildId(long guildId) {
        List<GuildMember> memberList = memberDAO.getAllMemberByGuildId(guildId);
        if (CollectionUtils.isNotEmpty(memberList)) {
            for (GuildMember member : memberList) {
                member.deserialize();
                ConcurrentUtils.putIfAbsent(playerId2GuildMember, member.getPlayerId(), member);
            }
        }
        return memberList;
    }

    public GuildMember getMemberInfoByPlayerId(long playerId) {
        return playerId2GuildMember.get(playerId);
    }

    public GuildMember cerateNewGuildMember(PlayerActor playerActor, long guildId) {
        GuildMember guildMember = new GuildMember();
        guildMember.insert();
        ConcurrentUtils.putIfAbsent(playerId2GuildMember, guildMember.getPlayerId(), guildMember);
        return guildMember;
    }

    public boolean addNewMemeber(long guildId, long playerId) {
        if (guildId == 0) return false;
        GuildMember guildMember = getMemberInfoByPlayerId(playerId);
        if (guildMember == null) return false;

        synchronized (guildMember) {
            if (guildMember.getGuildId() != 0) return false;
            guildMember.setGuildId(guildId);
            guildMember.setJoinTime(TimeUtils.timestamp());
            guildMember.update();
        }
        return true;
    }
    @Override
    public boolean insert(GuildMember object) {
        memberDAO.insert(object);
        return true;
    }

    @Override
    public boolean update(GuildMember object) {
        memberDAO.update(object);
        return true;
    }

    @Override
    public boolean delete(GuildMember object) {
        memberDAO.delete(object);
        return true;
    }
}
