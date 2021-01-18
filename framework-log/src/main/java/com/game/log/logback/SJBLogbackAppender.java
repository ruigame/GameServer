package com.game.log.logback;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.game.util.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * @Author: liguorui
 * @Date: 2020/12/2 下午9:34
 */
public class SJBLogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static Method collectMethod;
    private static Method shutdownMethod;
    private ThrowableProxyConverter throwableProxyConverter;

    @Override
    public void start() {
        try {
            Class<?> logClass = Class.forName("com.xiaosan.profiler.log.LogCollector");
            collectMethod = logClass.getDeclaredMethod("collect", String.class, String.class, String.class);
            shutdownMethod = logClass.getDeclaredMethod("shutdown");
            this.throwableProxyConverter = new ThrowableProxyConverter();
            throwableProxyConverter.start();
            super.start();
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionUtils.log(e);
        }
    }

    @Override
    public void stop() {
        if (shutdownMethod != null) {
            try {
                shutdownMethod.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (throwableProxyConverter != null) {
            throwableProxyConverter.stop();
        }
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent event) {
        String stackMsg = throwableProxyConverter.convert(event);
        String exceptionName = event.getFormattedMessage();
        if (StringUtils.isBlank(exceptionName)) {
            exceptionName = event.getThrowableProxy().getClassName();
        }
        String threadName = event.getThreadName();
        try {
            collectMethod.invoke(null, threadName, exceptionName, stackMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
