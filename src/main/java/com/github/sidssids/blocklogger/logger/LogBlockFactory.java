package com.github.sidssids.blocklogger.logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class LogBlockFactory {
    
    private LogBlockFactory() {}
    
    private static final LogBlock EMPTY = new LogBlock(null, null, null, null);
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
    
    public static LogBlock create(Class<?> loggerClass, Level level, String blockName) {
        return create(LoggerFactory.getLogger(loggerClass), level, blockName, null);
    }
    
    public static LogBlock create(Class<?> loggerClass, Level level, String blockName, String paramsFormat, Object... paramsValues) {
        return create(LoggerFactory.getLogger(loggerClass), level, blockName, paramsFormat, paramsValues);
    }

    public static LogBlock create(String loggerName, Level level, String blockName) {
        return create(LoggerFactory.getLogger(loggerName), level, blockName, null);
    }
    
    public static LogBlock create(String loggerName, Level level, String blockName, String paramsFormat, Object... paramsValues) {
        return create(LoggerFactory.getLogger(loggerName), level, blockName, paramsFormat, paramsValues);
    }
    
    private static LogBlock create(Logger logger, Level level, String blockName, String paramsFormat, Object... paramsValues) {
        check(logger, "logger");
        check(level, "level");
        check(blockName, "blockName");
        if (LEVEL_CHECK.get(level).test(logger)) {
            return new LogBlock(logger, level, blockName, paramsFormat != null ? String.format(paramsFormat, paramsValues) : null);
        } else {
            return EMPTY;
        }
    }
    
    private static void check(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(String.format("Argument '%s' is null", name));
        }
    }
}
