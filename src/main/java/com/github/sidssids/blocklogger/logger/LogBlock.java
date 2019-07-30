package com.github.sidssids.blocklogger.logger;

import com.github.sidssids.blocklogger.formatter.MarkerFormatter;
import com.github.sidssids.blocklogger.logger.markers.CloseMarker;
import com.github.sidssids.blocklogger.logger.markers.StartMarker;
import java.io.Closeable;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;

public class LogBlock implements Logger, Closeable {
    
    private final Level     level;
    private       Level     disposeLevel;
    private final Logger    logger;
    private final String    title;
    private final Instant   start;
    private       Throwable exception;
    private       String    result;
    
    LogBlock(Logger logger, Level level, String title, String params) {
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
            CloseMarker marker = new CloseMarker(title)
                    .withDuration(Duration.between(start, Instant.now()))
                    .withResult(result)
                    .withException(exception);
            String message = MarkerFormatter.generateCloseBlockMessage(marker);
            log(disposeLevel, marker, message);
        }
    }
    
    private void initialize(String params) {
        if (!isEmptyBlock()) {
            StartMarker marker  = new StartMarker(title).withParams(params);
            String      message = MarkerFormatter.generateOpenBlockMessage(marker);
            log(level, marker, message);
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

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        logger.trace(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        logger.trace(format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        logger.trace(format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        logger.trace(format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logger.trace(msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        logger.trace(marker, msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        logger.trace(marker, format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        logger.trace(marker, format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        logger.trace(marker, format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        logger.trace(marker, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        logger.debug(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        logger.debug(format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        logger.debug(format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        logger.debug(format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logger.debug(msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        logger.debug(marker, msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        logger.debug(marker, format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        logger.debug(marker, format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        logger.debug(marker, format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        logger.debug(marker, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        logger.info(format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        logger.info(format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        logger.info(format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.info(msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        logger.info(marker, msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        logger.info(marker, format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        logger.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        logger.info(marker, format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        logger.info(marker, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        logger.warn(format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        logger.warn(format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        logger.warn(marker, msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        logger.warn(marker, format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        logger.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        logger.warn(marker, format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        logger.warn(marker, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        logger.error(format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        logger.error(format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        logger.error(marker, msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        logger.error(marker, format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        logger.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        logger.error(marker, format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        logger.error(marker, msg, t);
    }

}
