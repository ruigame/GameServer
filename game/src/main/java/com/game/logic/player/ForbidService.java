package com.game.logic.player;

import com.game.PacketId;
import com.game.logic.common.ConfigService;
import com.game.logic.common.PlayerActor;
import com.game.logic.player.domain.forbid.Forbid;
import com.game.logic.player.entity.ForBidIpEntity;
import com.game.logic.player.entity.ForbidRoleEntity;
import com.game.logic.player.manager.ForbidEntityManager;
import com.game.logic.player.manager.ForbidIpEntityManager;
import com.game.logic.player.manager.MutePlayerManager;
import com.game.logic.player.packet.resp.RespForbidedMessagePacket;
import com.game.net.packet.PacketFactory;
import com.game.util.ServerStarter;
import com.game.util.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 后台封禁/解封
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:39
 */
@Component
public class ForbidService implements IForbidService, ServerStarter {

    private Logger log = LoggerFactory.getLogger(ForbidService.class);

    @Autowired
    private ForbidIpEntityManager forbidIpEntityManager;

    @Autowired
    private ForbidEntityManager forbidEntityManager;

    @Autowired
    private MutePlayerManager mutePlayerManager;

    @Autowired
    private ConfigService configService;

    private ForbidRoleEntity getOrCreateForbidRole(PlayerActor playerActor) {
        ForbidRoleEntity forbid = forbidEntityManager.getForbidRoleEntityBykey(playerActor.getAccount(), playerActor.getServer());
        return forbid;
    }

    /**
     * 封号
     * @param playerActor
     * @param limitTime
     * @param reason
     */
    @Override
    public void forbidRole(PlayerActor playerActor, int limitTime, String reason) {
        int forbidTime = TimeUtils.timestamp();
        ForbidRoleEntity forbid = getOrCreateForbidRole(playerActor);
        forbid.setLimitTime(limitTime);
        forbid.setForbidTime(forbidTime);
        forbid.setReason(reason);
        forbid.update();
    }

    /**
     * 解封
     * @param playerActor
     */
    @Override
    public void clearForbidRole(PlayerActor playerActor) {
        ForbidRoleEntity forbid = getForbidRole(playerActor);
        if (forbid == null) {
            return;
        }
        forbidEntityManager.deleteForbidRoleEntity(forbid);
    }

    private ForbidRoleEntity getForbidRole(PlayerActor playerActor) {
        ForbidRoleEntity forbid = forbidEntityManager.getNotInsert(playerActor.getAccount(), playerActor.getServer());
        return forbid;
    }

    /**
     * 是否封禁
     * @param playerActor
     * @return
     */
    @Override
    public boolean isRoleForbided(PlayerActor playerActor) {
        final ForbidRoleEntity forbid = getForbidRole(playerActor);
        if (forbid == null) return false;
        boolean flag = isForbidTimeOut(forbid);
        if (!flag) {
            log.warn("角色ID：{} 正在封禁中........", playerActor.getId());
        } else {
            forbidEntityManager.deleteForbidRoleEntity(forbid);
        }
        return !flag;
    }

    private boolean isForbidTimeOut(Forbid forbid) {
        int utime = TimeUtils.timestamp();
        return forbid.isForbidTimeOut(utime);
    }

    @Override
    public RespForbidedMessagePacket createRoleForbidedPacket(PlayerActor playerActor) {
        final ForbidRoleEntity forbid = getForbidRole(playerActor);
        if (forbid == null) {
            return null;
        }

        int utime = TimeUtils.timestamp();
        int []lessTime = forbidedLessTime(forbid.forbidLessTime(utime));

        RespForbidedMessagePacket packet = PacketFactory.createPacket(PacketId.Base.RESP_FORBIDED_TIME_LESS);
        packet.init(lessTime, forbid.getReason());
        return packet;
    }

    @Override
    public RespForbidedMessagePacket createRoleForbidedIPPacket(PlayerActor playerActor, String ip) {
        ForBidIpEntity forBidIpEntity = forbidIpEntityManager.get(ip);
        if (forBidIpEntity != null) {
            int utime = TimeUtils.timestamp();
            int[]lessTime = forbidedLessTime(forBidIpEntity.forbidLessTime(utime));
            RespForbidedMessagePacket packet = PacketFactory.createPacket(PacketId.Base.RESP_FORBIDED_TIME_LESS);
            packet.init(lessTime, forBidIpEntity.getReason());
            return packet;
        }
        return null;
    }

    public int[] forbidedLessTime(int lessTime) {
        int []time = new int[3];
        if (lessTime <= 0) {
            return time;
        }
        time[0] = lessTime / (24 * 60);
        time[1] = (lessTime % (24 * 60)) / 60;
        time[2] = lessTime % 60;
        return time;
    }

    @Override
    public void mutePlayer(long playerId, int muteTime, String reason) {
        mutePlayerManager.handle(playerId, muteTime, reason);
    }

    @Override
    public boolean isMute(long playerId) {
        return mutePlayerManager.isPlayerMute(playerId);
    }

    @Override
    public void banIps(String ips, int second, String reason) {
        String []iparrs = StringUtils.split(ips, ",");
        int serverId = configService.getOriServerId();
        for (String ip : iparrs) {
            ForBidIpEntity forBidIpEntity = forbidIpEntityManager.getForBidIpEntityByIp(ip, serverId);
            forBidIpEntity.updateForbidIp(TimeUtils.timestamp(), second, reason);
        }
    }

    @Override
    public void clearBanIps(String ips) {
        String []iparrs = StringUtils.split(ips, ",");
        for (String ip : iparrs) {
            forbidIpEntityManager.deleteForBidIpEntity(ip);
        }
    }

    /**
     * 是否被封
     * @param ip
     * @return
     */
    @Override
    public boolean isIPBan(String ip) {
        ForBidIpEntity forBidIpEntity = forbidIpEntityManager.get(ip);
        if (forBidIpEntity == null) {
            return false;
        }
        int utime = TimeUtils.timestamp();
        boolean flag = forBidIpEntity.isForbidTimeOut(utime);
        if (flag) {
            forBidIpEntity.delete();
        }
        return !flag;
    }

    @Override
    public int getOrder() {
        return COMMON;
    }

    @Override
    public void init() {
        forbidIpEntityManager.updateDataByServerId(configService.getOriServerId(), 0);
    }
}
