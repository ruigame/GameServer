package com.game.logic.guild.dao;

import com.game.async.asyncdb.orm.GameDaoSupport;
import com.game.logic.guild.entity.Guild;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2021/1/14 下午10:25
 */
@Component
@Transactional
public class GuildDAOImpl extends GameDaoSupport<Guild> implements GuildDAO {

    @Override
    public long getMaxId() {
        Session session = getSession();
        Long maxId = session.createNamedQuery("getMaxGuildId", Long.class).uniqueResult();
        return maxId == null ? 0 : maxId;
    }

    @Override
    public Guild getGuildInfoById(long guildId) {
        return get(guildId);
    }

    @Override
    public List<Guild> getAllValidGuilds() {
        Session session = getSession();
        return session.createNamedQuery("getAllValidGuilds", Guild.class).list();
    }

    @Override
    public int countByName(String guildName) {
        Session session = getSession();
        Long count = session.createNamedQuery("countByGuildName", Long.class).setParameter("name", guildName).uniqueResult();
        return count == null ? 0 : count.intValue();
    }

    @Override
    public List<Guild> getDuplicate() {
        Session session = getSession();
        return session.createNamedQuery("getDuplicateGuildName", Guild.class).list();
    }

    @Override
    public List<Guild> getDuplicateName() {
        Session session = getSession();
        return session.createQuery("select g1 from Guild g1 where g1.status > 0 and exists (" +
                " select name from Guild g2 " +
                " where g2.status > 0 and g1.name = g2.name and g1.id<>g2.id )" +
                " ORDER BY name", Guild.class).list();
    }
}
