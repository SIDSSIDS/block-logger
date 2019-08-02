package com.github.sidssids.blocklogger.logger.markers;

import java.time.Duration;
import java.util.Optional;

public class CloseMarker extends BaseMarker {
    
    private Duration            duration;
    private Optional<String>    result;
    private Optional<Throwable> exception;
    private Optional<Boolean>   appendExceptionInfo = Optional.empty();
    private Optional<Boolean>   appendStackTrace    = Optional.empty();
    
    public CloseMarker(String title) {
        super(CloseMarker.class.getSimpleName(), title);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Optional<String> getResult() {
        return result;
    }

    public void setResult(Optional<String> result) {
        this.result = result;
    }

    public Optional<Throwable> getException() {
        return exception;
    }

    public Optional<Boolean> getAppendExceptionInfo() {
        return appendExceptionInfo;
    }

    public void setAppendExceptionInfo(Optional<Boolean> appendExceptionInfo) {
        this.appendExceptionInfo = appendExceptionInfo;
    }

    public Optional<Boolean> getAppendStackTrace() {
        return appendStackTrace;
    }

    public void setAppendStackTrace(Optional<Boolean> appendStackTrace) {
        this.appendStackTrace = appendStackTrace;
    }

    public void setException(Optional<Throwable> exception) {
        this.exception = exception;
    }

    public CloseMarker withDuration(final Duration duration) {
        this.duration = duration;
        return this;
    }

    public CloseMarker withResult(final String result) {
        this.result = Optional.ofNullable(result);
        return this;
    }

    public CloseMarker withException(final Throwable exception) {
        this.exception = Optional.ofNullable(exception);
        return this;
    }
    
    public CloseMarker appendExceptionInfo(Boolean appendExceptionInfo) {
        this.appendExceptionInfo = Optional.ofNullable(appendExceptionInfo);
        return this;
    }
    
    public CloseMarker appendStackTrace(Boolean appendStackTrace) {
        this.appendStackTrace = Optional.ofNullable(appendStackTrace);
        return this;
    }
    
}
