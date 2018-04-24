package com.kolychev.utils.blocklogger.layout.tools;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;

public class LoggingEventCloner {
    
    private LoggingEventCloner() {
    }
    
    public static ILoggingEvent clone(ILoggingEvent source, String message) {
        return clone(source, source.getLevel(), message, source.getTimeStamp(), source.getThrowableProxy());
    }
    
    public static ILoggingEvent clone(ILoggingEvent source, Level level, String message, long timestamp, IThrowableProxy throwableProxy) {
        LoggingEvent event = new LoggingEvent();
        event.setArgumentArray(source.getArgumentArray());
        event.setCallerData(source.getCallerData());
        event.setLevel(level);
        event.setLoggerContextRemoteView(source.getLoggerContextVO());
        event.setLoggerName(source.getLoggerName());
        event.setMDCPropertyMap(source.getMDCPropertyMap());
        event.setMarker(source.getMarker());
        event.setMessage(message);
        event.setThreadName(source.getThreadName());
        event.setThrowableProxy((ThrowableProxy)throwableProxy);
        event.setTimeStamp(timestamp);
        return event;
    }
}
