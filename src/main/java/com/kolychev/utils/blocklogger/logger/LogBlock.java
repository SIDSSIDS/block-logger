package com.kolychev.utils.blocklogger.logger;

import com.kolychev.utils.blocklogger.logger.markers.CloseMarker;
import com.kolychev.utils.blocklogger.logger.markers.StartMarker;
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
    
    public void reportSuccess() {
        reportSuccess(null);
    }
    
    public void reportSuccess(String message) {
        result = message;
        disposeLevel = level;
    }
    
    public void reportSuccess(String message, Object... params) {
        result = message != null ? String.format(message, params) : message;
        disposeLevel = level;
    }
    
    public void reportTrace() {
        reportTrace(null);
    }
    
    public void reportTrace(String message) {
        result = message;
        disposeLevel = Level.TRACE;
    }
    
    public void reportTrace(String message, Object... params) {
        result = message != null ? String.format(message, params) : message;
        disposeLevel = Level.TRACE;
    }
    
    public void reportDebug() {
        reportDebug(null);
    }
    
    public void reportDebug(String message) {
        result = message;
        disposeLevel = Level.DEBUG;
    }
    
    public void reportDebug(String message, Object... params) {
        result = message != null ? String.format(message, params) : message;
        disposeLevel = Level.DEBUG;
    }
    
    public void reportInfo() {
        reportInfo(null);
    }
    
    public void reportInfo(String message) {
        result = message;
        disposeLevel = Level.INFO;
    }
    
    public void reportInfo(String message, Object... params) {
        result = message != null ? String.format(message, params) : message;
        disposeLevel = Level.INFO;
    }
    
    public void reportWarning() {
        reportWarning(null);
    }
    
    public void reportWarning(String message) {
        result = message;
        disposeLevel = Level.WARN;
    }
    
    public void reportWarning(String message, Object... params) {
        result = message != null ? String.format(message, params) : message;
        disposeLevel = Level.WARN;
    }
    
    public void reportError() {
        reportError("Error");
    }
    
    public void reportError(String message) {
        result = message;
        disposeLevel = Level.ERROR;
    }
    
    public void reportError(String message, Object... params) {
        result = message != null ? String.format(message, params) : message;
        disposeLevel = Level.ERROR;
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
