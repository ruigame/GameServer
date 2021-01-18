package com.game.log;

/**
 * 日志基类，其集成日志类必须以LogEvent结尾，自动生成除LogEvent外的类名的日志文件
 * @author liguorui
 * @date 2017/8/20 16:29
 */
public abstract class LogEvent {

    protected boolean robot; //是否机器人
    protected String fileName;
    protected String loggerName;
    protected String appenderName;

    public abstract String message();

    public String getLogFileName() {
        if (fileName == null) {
            String className = getClass().getSimpleName();
            int lastIndex = className.lastIndexOf("LogEvent");
            fileName = lastIndex == - 1 ? className : className.substring(0,lastIndex);
        }
        return fileName;
    }

    public String getLoggerName() {
        if (loggerName == null) {
            loggerName = getLogFileName() + "Logger";
        }
        return loggerName;
    }

    public String getAppenderName() {
        if (appenderName == null) {
            appenderName = "async" + getLoggerName() + "Appender";
        }
        return appenderName;
    }

    public boolean isRobot() {
        return robot;
    }

    public void setRobot(boolean robot) {
        this.robot = robot;
    }

    public void post() {
        if (robot) {
            return;
        }
//        Context.getLogEventHandler().post(this);
    }
}
