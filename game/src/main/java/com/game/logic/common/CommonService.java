package com.game.logic.common;

import com.game.logic.base.CronTaskType;
import com.game.logic.base.CronTriggerTask;
import com.game.logic.server.domain.ConfigKey;
import com.game.logic.server.entity.ServerInfo;
import com.game.logic.server.manager.ServerInfoManager;
import com.game.util.ServerStarter;
import com.game.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2021/1/19 下午11:59
 */
@Component
public class CommonService implements ServerStarter {

    @Autowired
    private ServerInfoManager serverInfoManager;

    @Autowired
    private MidNightService midNightService;

    @CronTriggerTask(CronTaskType.ZERO_TIME)
    public void onZeroTime() {
        long curTime = TimeUtils.currentTimeMillis();
//        ListenerHandler.getInstance().fireZeroTime(curTime);
        midNightService.onZero();

    }

    public void dailyResetOnServerStart() {
        long curTime = TimeUtils.currentTimeMillis();
        ServerInfo lastDailyResetTimeInfo = this.serverInfoManager.getConfig(ConfigKey.LAST_DAILY_RESET_TIME);
        long lastDailyResetTime = Long.parseLong(lastDailyResetTimeInfo.getCvalue());

        if (!TimeUtils.isSameDay(lastDailyResetTime, curTime)) {
            this.dailyReset(curTime);
        }
    }

    private void dailyReset(long curTime) {
        ServerInfo lastDailyResetTimeInfo = this.serverInfoManager.getConfig(ConfigKey.LAST_DAILY_RESET_TIME);
        lastDailyResetTimeInfo.setCvalue(String.valueOf(curTime));
        lastDailyResetTimeInfo.update();
//        ListenerHandler.getInstance().fireDailyUpdate(TimeUtils.currentTimeMillis());
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void init() {

    }
}
