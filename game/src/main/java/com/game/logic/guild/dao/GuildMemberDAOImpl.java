package com.game.logic.guild.dao;

import com.game.async.asyncdb.orm.GameDaoSupport;
import com.game.logic.guild.entity.GuildMember;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2021/1/14 下午10:58
 */
@Component
@Transactional
public class GuildMemberDAOImpl extends GameDaoSupport<GuildMember> implements GuildMemberDAO {


    @Override
    public List<GuildMember> getAllNoGuildMembers() {
        Session session = getSession();
        return session.createNamedQuery("getAllNoGuildMembers", GuildMember.class).list();
    }

    @Override
    public GuildMember getByPlayerId(long playerId) {
        return get(playerId);
    }

    @Override
    public List<GuildMember> getAllMemberByGuildId(long guildId) {
        Session session = getSession();
        return session.createNamedQuery("getByGuldId", GuildMember.class).setParameter("guildId", guildId).list();
    }
}
