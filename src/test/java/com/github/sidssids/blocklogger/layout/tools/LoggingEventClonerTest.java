package com.github.sidssids.blocklogger.layout.tools;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.github.sidssids.blocklogger.logger.markers.StartMarker;
import java.util.HashMap;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class LoggingEventClonerTest {

    @Test
    public void test_cloneMessage() {
        LoggingEvent event = new LoggingEvent();
        
        event.setArgumentArray(new Integer[]{1, 2, 3});
        event.setCallerData(new StackTraceElement[0]);
        event.setLevel(Level.DEBUG);
        event.setLoggerContextRemoteView(new LoggerContextVO("name", new HashMap<String, String>(), 1));
        event.setLoggerName("logger");
        event.setMDCPropertyMap(new HashMap<>());
        event.setMarker(new StartMarker("title", "params"));
        event.setMessage("message");
        event.setThreadName("threadName");
        event.setThrowableProxy(new ThrowableProxy(new RuntimeException("exmessage")));
        event.setTimeStamp(5);
        
        ILoggingEvent clone = LoggingEventCloner.clone(event, "new message");
        
        assertEquals(clone.getArgumentArray(), event.getArgumentArray());
        assertEquals(clone.getCallerData(), event.getCallerData());
        assertEquals(clone.getLevel(), event.getLevel());
        assertEquals(clone.getLoggerContextVO().getName(), event.getLoggerContextVO().getName());
        assertEquals(clone.getLoggerContextVO().getBirthTime(), event.getLoggerContextVO().getBirthTime());
        assertEquals(clone.getLoggerContextVO().getPropertyMap(), event.getLoggerContextVO().getPropertyMap());
        assertEquals(clone.getLoggerName(), event.getLoggerName());
        assertEquals(clone.getMDCPropertyMap(), event.getMDCPropertyMap());
        assertEquals(clone.getMarker(), event.getMarker());
        assertEquals(clone.getMessage(), "new message");
        assertEquals(clone.getThreadName(), event.getThreadName());
        assertEquals(clone.getThrowableProxy(), event.getThrowableProxy());
        assertEquals(clone.getTimeStamp(), event.getTimeStamp());
    }

    @Test
    public void test_cloneMessageLevel() {
        LoggingEvent event = new LoggingEvent();
        
        event.setArgumentArray(new Integer[]{1, 2, 3});
        event.setCallerData(new StackTraceElement[0]);
        event.setLevel(Level.DEBUG);
        event.setLoggerContextRemoteView(new LoggerContextVO("name", new HashMap<String, String>(), 1));
        event.setLoggerName("logger");
        event.setMDCPropertyMap(new HashMap<>());
        event.setMarker(new StartMarker("title", "params"));
        event.setMessage("message");
        event.setThreadName("threadName");
        event.setThrowableProxy(new ThrowableProxy(new RuntimeException("exmessage")));
        event.setTimeStamp(5);
        
        ILoggingEvent clone = LoggingEventCloner.clone(event, Level.INFO, "new message", 10, null);
        
        assertEquals(clone.getArgumentArray(), event.getArgumentArray());
        assertEquals(clone.getCallerData(), event.getCallerData());
        assertEquals(clone.getLevel(), Level.INFO);
        assertEquals(clone.getLoggerContextVO().getName(), event.getLoggerContextVO().getName());
        assertEquals(clone.getLoggerContextVO().getBirthTime(), event.getLoggerContextVO().getBirthTime());
        assertEquals(clone.getLoggerContextVO().getPropertyMap(), event.getLoggerContextVO().getPropertyMap());
        assertEquals(clone.getLoggerName(), event.getLoggerName());
        assertEquals(clone.getMDCPropertyMap(), event.getMDCPropertyMap());
        assertEquals(clone.getMarker(), event.getMarker());
        assertEquals(clone.getMessage(), "new message");
        assertEquals(clone.getThreadName(), event.getThreadName());
        assertEquals(clone.getThrowableProxy(), null);
        assertEquals(clone.getTimeStamp(), 10);
    }    
}
