package com.game.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.game.log.logback.DailyFileAppender;
import com.game.util.ExceptionUtils;
import com.game.util.ICloseEvent;
import com.game.util.SimpleThreadFactory;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author liguorui
 * @date 2017/8/20 16:23
 */
@Component
public class LogEventHandler implements ICloseEvent {

    private ExecutorService executorService = Executors.newSingleThreadExecutor(new SimpleThreadFactory("LogEventHandler"));

    private LinkedTransferQueue<LogEvent> logEventQueue = new LinkedTransferQueue<>();

    private volatile boolean running = true;

    private LogEvent END_EVENT = new LogEvent() {

        @Override
        public String message() {
            return null;
        }
    };

    @PostConstruct
    public void init() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        LogEvent logEvent = logEventQueue.take();
                        if (logEvent == END_EVENT) {
                            break;
                        }
                        Logger logger = getLogger(logEvent);
                        logger.info(logEvent.message());
                    } catch (Exception e) {
                        ExceptionUtils.log(e);
                    }
                }
            }
        });
    }

    public void post(LogEvent logEvent) {
        Preconditions.checkArgument(running);
        if (!logEvent.isRobot()) {
            logEventQueue.add(logEvent);
        }
    }

    private Logger getLogger(LogEvent logEvent) {
        Logger logger = LoggerFactory.getLogger(logEvent.getLoggerName());
        if (!(logger instanceof ch.qos.logback.classic.Logger)) {
            return logger;
        }

        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger)logger;
        if (logbackLogger.getAppender(logEvent.getAppenderName()) != null) {
            return logger;
        }
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        DailyFileAppender<ILoggingEvent> dailyFileAppender = new DailyFileAppender<>();
        dailyFileAppender.setContext(lc);
        dailyFileAppender.setName(logEvent.getLoggerName());
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(lc);
        encoder.setPattern("%m%n");
        encoder.start();
        dailyFileAppender.setEncoder(encoder);
        dailyFileAppender.setOriFileName(System.getProperty("log.dir","logs") + File.separator + logEvent.getLogFileName() + ".log");
        dailyFileAppender.start();

//        AsyncAppender syncAppender = new AsyncAppender();
//        syncAppender.setContext(lc);
//        syncAppender.setName(logEvent.getAppenderName());
//        syncAppender.addAppender(dailyFileAppender);
//        syncAppender.start();
//        logbackLogger.addAppender(syncAppender);

        logbackLogger.addAppender(dailyFileAppender);
        logbackLogger.setLevel(Level.INFO);
        logbackLogger.setAdditive(false);
        return logger;
    }

    @Override
    public void onServerClose() {
        running = false;
        logEventQueue.add(END_EVENT);
        try {
            executorService.shutdown();
            executorService.awaitTermination(20000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
