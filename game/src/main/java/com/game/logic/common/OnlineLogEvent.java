package com.game.logic.common;

import com.game.log.LogAppender;
import com.game.log.LogEvent;
import com.game.util.TimeUtils;

/**
 * @Author: liguorui
 * @Date: 2021/1/20 下午8:34
 */
public class OnlineLogEvent extends LogEvent {

    private int current;
    private int maxOnline;
    private long time;

    public OnlineLogEvent(int current, int maxOnline) {
        super();
        this.current = current;
        this.maxOnline = maxOnline;
        this.time = TimeUtils.currentTimeMillis();
    }

    @Override
    public String message() {
        LogAppender logAppender = LogAppender.create()
                .appendKeyValue("time", time)
                .appendKeyValue("current", current)
                .appendKeyValue("maxOnline", maxOnline);
        return logAppender.toString();
    }
}
