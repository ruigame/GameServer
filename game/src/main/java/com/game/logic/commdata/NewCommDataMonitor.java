package com.game.logic.commdata;

import com.game.logic.commdata.domain.accesser.IDataAccessor;
import com.game.logic.commdata.entity.NewCommData;
import com.game.util.TimeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liguorui
 * @date 2018/2/5 18:22
 */
public enum NewCommDataMonitor {

    DAILY() {

        @Override
        public void onLoad(NewCommData data) {
            if (StringUtils.isBlank(data.getMonitorParam())) {
                return;
            }
            int lastWriteTimestamp = Integer.valueOf(data.getMonitorParam());
            int lastWriteMorningTime = TimeUtils.getMorningTime(lastWriteTimestamp);
            if (lastWriteMorningTime != TimeUtils.getMorningTime()) {
                data.reset();
            }
        }

        @Override
        public void onWrite(NewCommData data) {
            data.setMonitorParam(String.valueOf(TimeUtils.timestamp()));
        }

        @Override
        public void onMidnight(NewCommData data, IDataAccessor accessor) {
            data.reset();
            accessor.reset();
        }
    }
    ;

    public void onRead(NewCommData data) {}
    public void onWrite(NewCommData data) {}
    public void onMidnight(NewCommData data, IDataAccessor accessor) {}
    public void onLoad(NewCommData data) {}
}
