package com.github.sidssids.blocklogger.logger;

import com.github.sidssids.blocklogger.logger.markers.CloseMarker;
import com.github.sidssids.blocklogger.logger.markers.StartMarker;
import java.io.Closeable;
import java.time.Duration;
import java.time.Instant;
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
    
    private final Level     level;
    private       Level     disposeLevel;
    private final Logger    logger;
    private final String    title;
    private final Instant   start;
    private       Throwable exception;
    private       String    result;
    private       boolean   skip = false;
    
    private LogBlock(Logger logger, Level level, String title, String params) {
        this.logger       = logger;
        this.title        = title;
        this.level        = level;
        this.start        = Instant.now();
        this.disposeLevel = level;
        initialize(params);
    }
    
    private boolean isEmptyBlock() {
        return logger == null;
    }
    
    public void skip() {
        skip = true;
    }
    
    public void cancelSkipping() {
        skip = false;
    }
    
    private void report(Level level) {
        report(level, null);
    }
    
    private void report(Level level, String message) {
        result       = message;
        disposeLevel = level;
    }
    
    private void report(Level level, String message, Object... params) {
        report(level, message != null ? String.format(message, params) : message);
    }
    
    public void reportSuccess() {
        report(level);
    }
    
    public void reportSuccess(String message) {
        report(level, message);
    }
    
    public void reportSuccess(String message, Object... params) {
        report(level, message, params);
    }
    
    public void reportTrace() {
        report(Level.TRACE);
    }
    
    public void reportTrace(String message) {
        report(Level.TRACE, message);
    }
    
    public void reportTrace(String message, Object... params) {
        report(Level.TRACE, message, params);
    }
    
    public void reportDebug() {
        report(Level.DEBUG);
    }
    
    public void reportDebug(String message) {
        report(Level.DEBUG, message);
    }
    
    public void reportDebug(String message, Object... params) {
        report(Level.DEBUG, message, params);
    }
    
    public void reportInfo() {
        report(Level.INFO);
    }
    
    public void reportInfo(String message) {
        report(Level.INFO, message);
    }
    
    public void reportInfo(String message, Object... params) {
        report(Level.INFO, message, params);
    }
    
    public void reportWarning() {
        report(Level.WARN);
    }
    
    public void reportWarning(String message) {
        report(Level.WARN, message);
    }
    
    public void reportWarning(String message, Object... params) {
        report(Level.WARN, message, params);
    }
    
    public void reportError() {
        report(Level.ERROR);
    }
    
    public void reportError(String message) {
        report(Level.ERROR, message);
    }
    
    public void reportError(String message, Object... params) {
        report(Level.ERROR, message, params);
    }
    
    public LogBlock withException(Throwable ex) {
        exception = ex;
        return this;
    }
    
    @Override
    public void close() {
        if (!isEmptyBlock()) {
            log(disposeLevel, new CloseMarker(title, Duration.between(Instant.now(), start), result, exception, skip), "");
        }
    }

    private void initialize(String params) {
        if (!isEmptyBlock()) {
            log(level, new StartMarker(title, params), "");
        }
    }
    
    private void log(Level level, Marker marker, String message) {
        switch(level) {
            case TRACE : logger.trace(marker, message, exception); break;
            case DEBUG : logger.debug(marker, message, exception); break;
            case INFO  : logger.info(marker, message, exception); break;
            case WARN  : logger.warn(marker, message, exception); break;
            case ERROR : logger.error(marker, message, exception); break;
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
