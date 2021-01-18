package com.game.log;

import com.google.common.base.Preconditions;

/**
 * @Author: liguorui
 * @Date: 2020/12/2 下午9:09
 */
public class LogAppender {

    private enum AppendStatus {
        KEY,VALUE
    }

    private static char SEPERATOR = 1;
    private static char MAP = ':';

    private StringBuilder sb;
    private AppendStatus status = AppendStatus.KEY;

    private char sp;

    private char mp;

    private LogAppender() {
        this(64);
    }

    private LogAppender(int capacity) {
        this(capacity, SEPERATOR, MAP);
    }

    private LogAppender(int capacity, char sp, char mp) {
        super();
        sb = new StringBuilder(capacity);
        this.sp = sp;
        this.mp = mp;
    }

    public LogAppender appendKey(CharSequence s) {
        Preconditions.checkArgument(status == AppendStatus.KEY);
        if (sb.length() > 0) {
            sb.append(sp);
        }
        sb.append(s).append(mp);
        status = AppendStatus.VALUE;
        return this;
    }

    public LogAppender appendValue(Object s) {
        Preconditions.checkArgument(status == AppendStatus.VALUE);
        sb.append(s);
        status = AppendStatus.KEY;
        return this;
    }

    public LogAppender appendKeyValue(CharSequence key, Object value) {
        appendKey(key);
        appendValue(value);
        return this;
    }

    @Override
    public String toString() {
        Preconditions.checkArgument(status == AppendStatus.KEY);
        return sb.toString();
    }

    public static LogAppender create() {
        return new LogAppender();
    }

    public static LogAppender create(int capacity) {
        return new LogAppender(capacity);
    }

    public static LogAppender create(int capacity, char sp, char mp) {
        return new LogAppender(capacity, sp, mp);
    }
}
