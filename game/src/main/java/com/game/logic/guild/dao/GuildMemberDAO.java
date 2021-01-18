package com.game.logic.guild.dao;

import com.game.async.asyncdb.orm.BaseDao;
import com.game.logic.guild.entity.GuildMember;

import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2021/1/14 下午10:55
 */
public interface GuildMemberDAO extends BaseDao<GuildMember> {

    List<GuildMember> getAllNoGuildMembers();

    GuildMember getByPlayerId(long playerId);

    List<GuildMember> getAllMemberByGuildId(long guildId);
}
