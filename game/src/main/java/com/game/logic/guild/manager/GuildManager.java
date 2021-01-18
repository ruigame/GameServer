package com.game.logic.guild.manager;

import com.game.async.asyncdb.Synchronizer;
import com.game.base.ConfigService;
import com.game.base.PlayerActor;
import com.game.base.UUIDService;
import com.game.logic.guild.GuildPooledBusinessService;
import com.game.logic.guild.dao.GuildDAO;
import com.game.logic.guild.dao.GuildMemberDAO;
import com.game.logic.guild.entity.Guild;
import com.game.logic.guild.entity.GuildMember;
import com.game.util.Context;
import com.game.util.ServerStarter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: liguorui
 * @Date: 2021/1/14 下午10:46
 */
@Component
public class GuildManager implements Synchronizer<Guild>, ServerStarter {

    private static Comparator<Guild> DEFAULT_GUILD_COMPARATOR = new Comparator<Guild>() {
        @Override
        public int compare(Guild o1, Guild o2) {
            if (o2.getLevel() != o1.getLevel()) {
                return o2.getLevel() - o1.getLevel();
            }

            if (Context.getBean(ConfigService.class).getDayAfterOpenServer() == 1) {
                return o2.getCreateTime() - o1.getCreateTime();
            } else {
                return o1.getCreateTime() - o2.getCreateTime();
            }
        }
    };

    @Autowired
    private GuildDAO guildDAO;
    @Autowired
    private GuildMemberDAO guildMemberDAO;
    @Autowired
    private UUIDService uuidService;
    @Autowired
    private GuildMemberManager guildMemberManager;
    @Autowired
    private GuildPooledBusinessService guildPooledBusinessService;

    /**
     * 所有公会id映射 id-Guild
     */
    private ConcurrentMap<Long, Guild> guildMap = new ConcurrentHashMap<>();

    /**
     * 所有公会名称映射 name-guildId
     */
    private ConcurrentMap<String, Long> name2IdMap = new ConcurrentHashMap<>();

    /**
     * 服务器启动加载所有公会信息
     */
    private void loadAllGuild() {
        List<Guild> allValidGuilds = guildDAO.getAllValidGuilds();
        if (CollectionUtils.isNotEmpty(allValidGuilds)) {
            for (Guild guild : allValidGuilds) {
                guild.deserialize();
                //加载所有公会成员
                List<GuildMember> allMembers = guildMemberDAO.getAllMemberByGuildId(guild.getId());
                if (CollectionUtils.isNotEmpty(allMembers)) {
                    for (GuildMember member : allMembers) {
                        guild.addMember(member.getPlayerId());
                    }
                }
                ConcurrentUtils.putIfAbsent(guildMap, guild.getId(), guild);
                ConcurrentUtils.putIfAbsent(name2IdMap, guild.getName(), guild.getId());
            }
        }
    }

    public void loadGuildName() {
        ConcurrentMap<String, Long> name2IdMap = new ConcurrentHashMap<>();
        for (Guild guild : guildMap.values()) {
            ConcurrentUtils.putIfAbsent(name2IdMap, guild.getName(), guild.getId());
        }
        this.name2IdMap = name2IdMap;
    }

    public Collection<Guild> getAllSortedGuilds() {
        List<Guild> guildList = new ArrayList<>(guildMap.values());
        Collections.sort(guildList, DEFAULT_GUILD_COMPARATOR);
        return Collections.unmodifiableCollection(guildList);
    }

    public long getMaxGuildId() {
        return guildDAO.getMaxId();
    }

    public synchronized Guild createNewGuild(PlayerActor playerActor, String guildName) {
        final long guildId = uuidService.makeUniqueGuildId();
        Guild guild = new Guild();
        if (guildMap.putIfAbsent(guild.getId(), guild) == null && name2IdMap.putIfAbsent(guildName, guild.getId()) == null) {
            GuildMember member = guildMemberManager.getMemberInfoByPlayerId(playerActor.getPlayerId());
            synchronized (member) {
                if (member == null) {
                    member = guildMemberManager.cerateNewGuildMember(playerActor, guildId);
                    member.update();
                } else {
                    member.setGuildId(guildId);
                    member.update();
                }
            }
            guild.addMember(member.getPlayerId());
            return guild;
        }
        return null;
    }

    @Override
    public boolean insert(Guild object) {
        return false;
    }

    @Override
    public boolean update(Guild object) {
        return false;
    }

    @Override
    public boolean delete(Guild object) {
        return false;
    }

    @Override
    public void init() {

    }
}
