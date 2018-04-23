package com.kolychev.utils.blocklogger.logger;

import com.kolychev.utils.blocklogger.logger.markers.CloseMarker;
import com.kolychev.utils.blocklogger.logger.markers.StartMarker;
import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.event.Level;

public class LogBlock implements Closeable {
    
    private static final Predicate<Logger> TRACE_ENABLED = Logger::isTraceEnabled;
    private static final Predicate<Logger> DEBUG_ENABLED = Logger::isDebugEnabled;
    private static final Predicate<Logger> INFO_ENABLED  = Logger::isInfoEnabled;
    private static final Predicate<Logger> WARN_ENABLED  = Logger::isWarnEnabled;
    private static final Predicate<Logger> ERROR_ENABLED = Logger::isErrorEnabled;
    private static final Map<Level, Predicate<Logger>> LEVEL_CHECK = new HashMap<>(5);
    static {
        LEVEL_CHECK.put(Level.TRACE, TRACE_ENABLED);
        LEVEL_CHECK.put(Level.DEBUG, DEBUG_ENABLED);
        LEVEL_CHECK.put(Level.INFO,  INFO_ENABLED);
        LEVEL_CHECK.put(Level.WARN,  WARN_ENABLED);
        LEVEL_CHECK.put(Level.ERROR, ERROR_ENABLED);
    }
    
    private static final LogBlock EMPTY = new LogBlock(null, null, null, null);
    
    private final Marker startMarker = new StartMarker();
    private final Marker closeMarker = new CloseMarker();
    
    private final Level  level;
    private final Logger logger;
    private final String title;
    
    private LogBlock(Logger logger, Level level, String title, String params) {
        this.logger = logger;
        this.title  = title;
        this.level  = level;
        initialize(params);
    }
    
    private boolean isEmptyBlock() {
        return logger == null;
    }
    
    @Override
    public void close() {
        if (!isEmptyBlock()) {
            log(closeMarker, String.format("[-] %s", title));
        }
    }

    private void initialize(String params) {
        if (!isEmptyBlock()) {
            StringBuilder sb = new StringBuilder();
            sb.append("[+] ").append(title);
            if (params != null) {
                sb.append(" (").append(params).append(")");
            }
            sb.append(": Started...");
            log(startMarker, sb.toString());
        }
    }
    
    private void log(Marker marker, String message) {
        switch(level) {
            case TRACE : logger.trace(marker, message); break;
            case DEBUG : logger.debug(marker, message); break;
            case INFO  : logger.info(marker, message); break;
            case WARN  : logger.warn(marker, message); break;
            case ERROR : logger.error(marker, message); break;
        }
    }

    public static LogBlock trace(Class<?> loggerClass, String blockName) {
        return create(LoggerFactory.getLogger(loggerClass), Level.TRACE, blockName, null);
    }
    
    public static LogBlock trace(Class<?> loggerClass, String blockName, String paramsFormat, Object... paramsValues) {
        return create(LoggerFactory.getLogger(loggerClass), Level.TRACE, blockName, paramsFormat, paramsValues);
    }

    public static LogBlock trace(String loggerName, String blockName) {
        return create(LoggerFactory.getLogger(loggerName), Level.TRACE, blockName, null);
    }
    
    public static LogBlock trace(String loggerName, String blockName, String paramsFormat, Object... paramsValues) {
        return create(LoggerFactory.getLogger(loggerName), Level.TRACE, blockName, paramsFormat, paramsValues);
    }
    
    public static LogBlock debug(Class<?> loggerClass, String blockName) {
        return create(LoggerFactory.getLogger(loggerClass), Level.DEBUG, blockName, null);
    }
    
    public static LogBlock debug(Class<?> loggerClass, String blockName, String paramsFormat, Object... paramsValues) {
        return create(LoggerFactory.getLogger(loggerClass), Level.DEBUG, blockName, paramsFormat, paramsValues);
    }

    public static LogBlock debug(String loggerName, String blockName) {
        return create(LoggerFactory.getLogger(loggerName), Level.DEBUG, blockName, null);
    }
    
    public static LogBlock debug(String loggerName, String blockName, String paramsFormat, Object... paramsValues) {
        return create(LoggerFactory.getLogger(loggerName), Level.DEBUG, blockName, paramsFormat, paramsValues);
    }
    
    public static LogBlock info(Class<?> loggerClass, String blockName) {
        return create(LoggerFactory.getLogger(loggerClass), Level.INFO, blockName, null);
    }
    
    public static LogBlock info(Class<?> loggerClass, String blockName, String paramsFormat, Object... paramsValues) {
        return create(LoggerFactory.getLogger(loggerClass), Level.INFO, blockName, paramsFormat, paramsValues);
    }

    public static LogBlock info(String loggerName, String blockName) {
        return create(LoggerFactory.getLogger(loggerName), Level.INFO, blockName, null);
    }
    
    public static LogBlock info(String loggerName, String blockName, String paramsFormat, Object... paramsValues) {
        return create(LoggerFactory.getLogger(loggerName), Level.INFO, blockName, paramsFormat, paramsValues);
    }
    
    public static LogBlock warn(Class<?> loggerClass, String blockName) {
        return create(LoggerFactory.getLogger(loggerClass), Level.WARN, blockName, null);
    }
    
    public static LogBlock warn(Class<?> loggerClass, String blockName, String paramsFormat, Object... paramsValues) {
        return create(LoggerFactory.getLogger(loggerClass), Level.WARN, blockName, paramsFormat, paramsValues);
    }

    public static LogBlock warn(String loggerName, String blockName) {
        return create(LoggerFactory.getLogger(loggerName), Level.WARN, blockName, null);
    }
    
    public static LogBlock warn(String loggerName, String blockName, String paramsFormat, Object... paramsValues) {
        return create(LoggerFactory.getLogger(loggerName), Level.WARN, blockName, paramsFormat, paramsValues);
    }
    
    public static LogBlock error(Class<?> loggerClass, String blockName) {
        return create(LoggerFactory.getLogger(loggerClass), Level.ERROR, blockName, null);
    }
    
    public static LogBlock error(Class<?> loggerClass, String blockName, String paramsFormat, Object... paramsValues) {
        return create(LoggerFactory.getLogger(loggerClass), Level.ERROR, blockName, paramsFormat, paramsValues);
    }

    public static LogBlock error(String loggerName, String blockName) {
        return create(LoggerFactory.getLogger(loggerName), Level.ERROR, blockName, null);
    }
    
    public static LogBlock error(String loggerName, String blockName, String paramsFormat, Object... paramsValues) {
        return create(LoggerFactory.getLogger(loggerName), Level.ERROR, blockName, paramsFormat, paramsValues);
    }
    
    private static LogBlock create(Logger logger, Level level, String blockName, String paramsFormat, Object... paramsValues) {
        if (LEVEL_CHECK.get(level).test(logger)) {
            return new LogBlock(logger, level, blockName, paramsFormat != null ? String.format(paramsFormat, paramsValues) : null);
        } else {
            return EMPTY;
        }
    }

}
