package com.game.logic.guild.dao;

import com.game.async.asyncdb.orm.BaseDao;
import com.game.logic.guild.entity.Guild;

import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2021/1/14 下午10:21
 */
public interface GuildDAO extends BaseDao<Guild> {

    long getMaxId();

    Guild getGuildInfoById(long guildId);

    List<Guild> getAllValidGuilds();

    int countByName(String guildName); //同一个服内避免名字相同

    List<Guild> getDuplicate(); //查询所有重名的工会

    /**
     * 查询公会名重复的（合服用）
     * @return
     */
    List<Guild> getDuplicateName();
}
