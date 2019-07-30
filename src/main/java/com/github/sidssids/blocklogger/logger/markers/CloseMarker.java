package com.github.sidssids.blocklogger.logger.markers;

import java.time.Duration;
import java.util.Optional;

public class CloseMarker extends BaseMarker {
    
    private Duration            duration;
    private Optional<String>    result;
    private Optional<Throwable> exception;
    
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
    
}
