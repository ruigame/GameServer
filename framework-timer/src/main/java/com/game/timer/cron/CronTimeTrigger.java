package com.game.timer.cron;

import com.game.timer.TimeTrigger;
import org.quartz.CronTrigger;

import java.text.ParseException;
import java.util.Date;

/**
 * cron 格式的时间触发器
 * @Author: liguorui
 * @Date: 2020/11/24 下午5:36
 */
public class CronTimeTrigger implements TimeTrigger {

    private long lastFireTime;

    private long fireTime;

    private String timeExpression;

    private CronTrigger trigger;

    public CronTimeTrigger(String timeExpression) throws ParseException {
        this(System.currentTimeMillis(), timeExpression);
    }

    public CronTimeTrigger(long startTimeMillis, String timeExpression) throws ParseException {
        this.timeExpression = timeExpression;
        long start = startTimeMillis > 0 ? startTimeMillis : System.currentTimeMillis();
        this.trigger = new CronTrigger();
        trigger.setCronExpression(timeExpression);
        setStartTime(start);
    }

    @Override
    public void setStartTime(long startTime) {
        trigger.setStartTime(new Date(startTime));
        trigger.setNextFireTime(new Date(startTime));
        trigger.triggered(null);
        this.lastFireTime = startTime;
        Date nextDate = this.trigger.getNextFireTime();
        if (nextDate == null) {
            fireTime = -1;
        } else {
            fireTime = nextDate.getTime();
        }
    }

    @Override
    public boolean trigger() {
        return trigger(System.currentTimeMillis());
    }

    @Override
    public boolean trigger(long time) {
        if (canTrigger() && time >= fireTime) {
            synchronized (this) {
                if (time >= fireTime) {
                    this.lastFireTime = fireTime;
                    trigger.triggered(null);
                    Date nextDate = this.trigger.getNextFireTime();
                    if (nextDate == null) {
                        fireTime = -1;
                    } else {
                        fireTime = nextDate.getTime();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public long getLastFireTime() {
        return lastFireTime;
    }

    @Override
    public long getTriggerTime() {
        return fireTime;
    }

    public String getTimeExpression() {
        return timeExpression;
    }

    @Override
    public long getLeftTime() {
        long now = System.currentTimeMillis();
        return now > fireTime ? 0 : fireTime - now;
    }

    @Override
    public boolean canTrigger() {
        return fireTime > 0;
    }
}
